package com.kaydev.appstore.services;

// import java.io.IOException;
// import java.io.PrintWriter;
// import java.net.Socket;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.messaging.MessageChannel;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.kaydev.appstore.models.entities.Distributor;
import com.kaydev.appstore.models.entities.Manufacturer;
import com.kaydev.appstore.models.entities.ManufacturerModel;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.entities.TerminalInfo;
import com.kaydev.appstore.models.entities.User;
import com.kaydev.appstore.utils.GenericUtil;

// import io.netty.channel.Channel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationService {
    @Value("${app.storeFront}")
    private String storeFront;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageChannel mqttOutboundChannel;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    // @Autowired
    // private TCPSocketSessionManager socketSessionManager;

    @Async("taskExecutor")
    public void sendWebSocket(Map<String, Object> message, String channelId) {
        try {
            simpMessagingTemplate.convertAndSend("/topic/" + channelId, message);
        } catch (Exception e) {
            log.error("Error sending WS message: " + e.getMessage(), e);
        }
    }

    // @Async("taskExecutor")
    // public void sendTCPSocket(Map<String, Object> data, String serialNumber) {
    // // log.info("Sending TCP message: " + data);
    // Socket clientSocket = socketSessionManager.getSession(serialNumber);

    // if (clientSocket != null && clientSocket.isConnected()) {
    // try {
    // String json = GenericUtil.convertMapToJsonString(data);

    // PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
    // writer.println(json);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // }

    // @Async("taskExecutor")
    // public void sendNettySocket(Map<String, Object> data, String serialNumber) {
    // Channel terminalChannel =
    // NettySocketServerHandler.getSocketBySN(serialNumber);
    // if (terminalChannel != null && terminalChannel.isActive()) {
    // try {
    // terminalChannel.writeAndFlush((Object)
    // GenericUtil.convertMapToJsonString(data));
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    // }

    @Async("taskExecutor")
    public void sendMQTT(Map<String, Object> data, String serialNumber) {
        String json = GenericUtil.convertMapToJsonString(data);
        String topic = "itexstore/mqtt/terminal/" + serialNumber;
        mqttOutboundChannel
                .send(MessageBuilder.withPayload(json).setHeader("mqtt_topic",
                        topic).build());
    }

    @Async("taskExecutor")
    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String content = "Dear " + user.getFullName()
                + "<br><br>Your account verification code is <br><br><span style=\"font-size: 20px; font-weight: bold; text-align: center;\">"
                + user.getVerificationToken()
                + "</span>.<br><br>If you did not request this action, please ignore this email. <br><br>Thank you.";

        sendEmail(user.getEmail(), subject, content);
    }

    @Async("taskExecutor")
    public void sendGeoFenceAlertEmail(Terminal terminal, Manufacturer manufacturer, ManufacturerModel model,
            Distributor distributor, TerminalInfo terminalInfo, List<String> emails) {
        try {

            String subject = "GeoFence Offline Alert";
            String content = "Terminal with serial number" + terminal.getSerialNumber()
                    + " has gone out of GeoFence coordinates.<br><br><table><tbody><tr><td>Serial Number:</td><td>"
                    + terminal
                            .getSerialNumber()
                    + "</td></tr><tr><td>Manufacture:</td><td" + manufacturer.getManufacturerName()
                    + "</td></tr><tr><td>Model:</td><td>" + model.getModelName()
                    + "</td></tr><tr><td>Distributor:</td><td>" + terminal.getDistributor().getDistributorName()
                    + "</td></tr><tr><td>Current Location:</td><td><a href=\"https://www.google.com/maps?q="
                    + terminal.getTerminalInfo().getLatitude() + "," + terminal
                            .getTerminalInfo().getLongitude()
                    + "&z=15&t=k\">View On Map</a></td></tr></tbody></table>";

            emails.forEach(email -> sendEmail(email, subject, content));
        } catch (Exception e) {

        }
    }

    @Async("taskExecutor")
    public void sendWelcomeEmail(User user, String password) {
        String subject = "Welcome to ITEX Store";
        String content = "Dear " + user.getFullName()
                + "<br><br>Your account has been created successfully. <br><br>Your Tempoary password is <b>" + password
                + "</b>.<br><br>To login to your account, please click on the link below. <br><br><a href=\""
                + storeFront + "\">ITEX Store Developer Account</a><br><br>Thank you.";

        sendEmail(user.getEmail(), subject, content);
    }

    @Async("taskExecutor")
    private void sendEmail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            Context context = new Context();
            context.setVariable("content", content);

            String htmlContent = templateEngine.process("email-content", context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            // Handle exception
            e.printStackTrace();
        }

    }

}
