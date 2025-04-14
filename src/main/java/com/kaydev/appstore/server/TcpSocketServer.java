package com.kaydev.appstore.server;
// package com.iisysgroup.itexstore.server;

// import java.util.concurrent.Executors;
// import java.util.concurrent.ScheduledExecutorService;
// import java.util.concurrent.TimeUnit;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import jakarta.annotation.PostConstruct;
// import jakarta.annotation.PreDestroy;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// public class TcpSocketServer {
// @Value("${server.socket.port}")
// private int port;

// // @Autowired
// // private TcpSocketServerHandler handler;

// // @PostConstruct
// // public void init() {
// // try {
// // handler.startServer(port);
// // } catch (Exception e) {
// // init();
// // }
// // }

// // @PreDestroy
// // public void shutdown() {
// // handler.stopServer();
// // }

// @Autowired
// private TcpSocketServerHandler handler;

// private final ScheduledExecutorService scheduler =
// Executors.newScheduledThreadPool(1);

// @PostConstruct
// public void init() {
// startServerWithRetry();
// }

// private void startServerWithRetry() {
// try {
// handler.startServer(port);
// } catch (Exception e) {
// log.error("Failed to start server on port " + port + ". Retrying in 10
// seconds...", e);
// scheduler.schedule(this::startServerWithRetry, 10, TimeUnit.SECONDS);
// }
// }

// @PreDestroy
// public void shutdown() {
// handler.stopServer();
// scheduler.shutdown();
// try {
// if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
// log.warn("Scheduler did not terminate in the specified time.");
// scheduler.shutdownNow();
// if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
// log.error("Scheduler did not terminate.");
// }
// }
// } catch (InterruptedException e) {
// log.error("Shutdown interrupted.", e);
// scheduler.shutdownNow();
// Thread.currentThread().interrupt();
// }
// }

// }
