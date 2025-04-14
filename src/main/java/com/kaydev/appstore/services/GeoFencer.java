package com.kaydev.appstore.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.kaydev.appstore.handlers.TaskHandler;
import com.kaydev.appstore.models.dto.request.is.CreateTaskRequest;
import com.kaydev.appstore.models.entities.Developer;
import com.kaydev.appstore.models.entities.Terminal;
import com.kaydev.appstore.models.enums.PushPeriod;
import com.kaydev.appstore.models.enums.PushTo;
import com.kaydev.appstore.models.enums.TaskType;
import com.kaydev.appstore.services.data.TerminalService;
import com.kaydev.appstore.services.data.UserService;

@Service
public class GeoFencer {
        private static final double EARTH_RADIUS = 6371e3;

        @Autowired
        private TaskScheduler taskScheduler;

        @Autowired
        private UserService userService;

        @Autowired
        private TaskHandler taskHandler;

        @Autowired
        private TerminalService terminalService;

        @Autowired
        private NotificationService notificationService;

        public boolean isOnline(double centerLatitude, double centerLongitude, double terminalLatitude,
                        double terminalLongitude,
                        double radius) {

                double distance = calculateDistance(centerLatitude, centerLongitude, terminalLatitude,
                                terminalLongitude);
                return distance <= radius;
        }

        private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
                double phi1 = Math.toRadians(lat1);
                double phi2 = Math.toRadians(lat2);
                double deltaPhi = Math.toRadians(lat2 - lat1);
                double deltaLambda = Math.toRadians(lon2 - lon1);

                double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                                Math.cos(phi1) * Math.cos(phi2) *
                                                Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

                return EARTH_RADIUS * c;
        }

        @Async
        public void pushGeoShutdown(String terminalUuid) {
                Terminal terminal = terminalService.getTerminalByUuid(terminalUuid);

                List<String> developerUserEmails = userService
                                .getDeveloperUserEmails(terminal.getDeveloper().getId());

                notificationService.sendGeoFenceAlertEmail(terminal, terminal.getManufacturer(), terminal.getModel(),
                                terminal.getDistributor(), terminal.getTerminalInfo(), developerUserEmails);

                List<Long> pushToTerminal = Arrays.asList(terminal.getId());

                List<Terminal> terminals = Arrays.asList(terminal);

                Developer developer = terminal.getDeveloper();

                CreateTaskRequest alertTask = CreateTaskRequest.builder()
                                .taskType(TaskType.PUSH_MESSAGE).pushPeriod(PushPeriod.IMMEDIATE)
                                .developerId(developer.getId()).terminaList(pushToTerminal).pushTo(PushTo.TERMINAL)
                                .message(
                                                "GeoFence Alert: Terminal is out of specified radius 🚧. This device will shutdown in the next 1 minute")
                                .build();

                CreateTaskRequest shutdownTask = CreateTaskRequest.builder()
                                .taskType(TaskType.SHUTDOWN_DEVICE).pushPeriod(PushPeriod.IMMEDIATE)
                                .developerId(developer.getId()).terminaList(pushToTerminal).pushTo(PushTo.TERMINAL)
                                .build();

                taskHandler.performTask(terminals, alertTask, developer.getUser(), developer);
                taskScheduler.schedule(
                                () -> taskHandler.performTask(terminals, shutdownTask, developer.getUser(), developer),
                                Instant.now().plusSeconds(60));
        }
}
