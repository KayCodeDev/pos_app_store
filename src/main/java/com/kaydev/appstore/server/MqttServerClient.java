package com.kaydev.appstore.server;

import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.kaydev.appstore.handlers.StoreHandler;
import com.kaydev.appstore.models.dto.request.store.TerminalSyncRequest;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.utils.GenericUtil;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MqttServerClient {
    @Value("${server.mqtt.username}")
    private String mqtt_username;

    @Value("${server.mqtt.password}")
    private String mqtt_password;

    @Value("${server.mqtt.host}")
    private String mqtt_host;

    @Value("${server.mqtt.topic}")
    private String mqtt_topic;

    @Value("${server.mqtt.qos}")
    private int mqtt_qos;

    @Value("${server.mqtt.env}")
    private String mqtt_env;

    @Autowired
    private ExecutorService executorService;

    @Lazy
    @Autowired
    private StoreHandler storeHandler;

    @Autowired
    private TerminalService terminalService;

    private final String clientId = "ItexStoreMQTTClient";

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { mqtt_host });
        options.setUserName(mqtt_username);
        options.setPassword(mqtt_password.toCharArray());
        options.setKeepAliveInterval(3000);
        options.setMaxInflight(50);
        options.setCleanSession(false);
        options.setAutomaticReconnect(true);
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "_out_" + mqtt_env,
                mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("itexstore/mqtt/terminal");
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttMessageDrivenChannelAdapter() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "_in_" + mqtt_env,
                mqttClientFactory(), mqtt_topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(mqtt_qos);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            try {
                Map<String, Object> dataMap = GenericUtil.convertJsonStringToMap(message.getPayload().toString());
                // log.info("Received MQTT message: {}", dataMap);

                if (!dataMap.containsKey("serialNumber") || !dataMap.containsKey("event")) {
                    log.warn("Invalid message payload: {}", dataMap);
                    return;
                }

                String serialNumber = dataMap.get("serialNumber").toString();
                String event = dataMap.get("event").toString();

                executorService.execute(() -> handleEvent(dataMap, serialNumber, event));
            } catch (Exception e) {
                log.error("Error processing MQTT message", e);
            }
        };
    }

    private void handleEvent(Map<String, Object> dataMap, String serialNumber, String event) {
        try {
            Terminal terminal = terminalService.getTerminalBySerialNumber(serialNumber);

            if (terminal == null) {
                log.warn("Terminal not found for serial number: {}", serialNumber);
                return;
            }

            // log.info("Processing event {} for terminal {}", event, serialNumber);

            switch (event.toLowerCase()) {
                case "terminalsync":
                    handleTerminalSync(dataMap, terminal);
                    break;
                case "updatetask":
                    handleUpdateTask(dataMap, terminal, serialNumber);
                    break;
                case "reportdownload":
                    handleReportDownload(dataMap, terminal);
                    break;
                default:
                    log.warn("Unhandled event type: {}", event);
            }
        } catch (Exception e) {
            log.error("Error handling event: {}", event, e);
        }
    }

    private void handleTerminalSync(Map<String, Object> dataMap, Terminal terminal) {
        dataMap.remove("event");
        dataMap.remove("serialNumber");

        TerminalSyncRequest syncRequest = GenericUtil.convertObjectToClass(dataMap, TerminalSyncRequest.class);
        storeHandler.syncTerminalMqtt(terminal, syncRequest);
        // log.info("Handled terminalSync event for terminal: {}",
        // terminal.getSerialNumber());
    }

    private void handleUpdateTask(Map<String, Object> dataMap, Terminal terminal, String serialNumber) {
        String taskId = (String) dataMap.get("taskId");
        String status = (String) dataMap.get("status");
        String taskMessage = (String) dataMap.get("message");
        String file = dataMap.containsKey("file") ? (String) dataMap.get("file") : null;

        storeHandler.updateTaskMqtt(terminal, serialNumber, taskId, status, taskMessage, file);
        // log.info("Handled updateTask event for task {} in terminal: {}", taskId,
        // terminal.getSerialNumber());
    }

    private void handleReportDownload(Map<String, Object> dataMap, Terminal terminal) {
        String appUuid = (String) dataMap.get("appUuid");
        String versionUuid = (String) dataMap.get("versionUuid");

        storeHandler.notifyDownloadMqtt(terminal, appUuid, versionUuid);
        // log.info("Handled reportDownload event for terminal: {}",
        // terminal.getSerialNumber());
    }

    // Optional: Shut down executor service on application shutdown
    @PreDestroy
    public void shutdownExecutor() {
        if (executorService != null) {
            executorService.shutdown();
            // log.info("ExecutorService shut down.");
        }
    }
}