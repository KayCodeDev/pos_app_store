package com.kaydev.appstore.server;
// package com.iisysgroup.itexstore.server;

// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
// import java.net.ServerSocket;
// import java.net.Socket;
// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Component;

// import com.iisysgroup.itexstore.models.entities.Terminal;
// import com.iisysgroup.itexstore.models.enums.StatusType;
// import com.iisysgroup.itexstore.server.managers.TCPSocketSessionManager;
// import com.iisysgroup.itexstore.services.data.TerminalService;
// import com.iisysgroup.itexstore.utils.GenericUtil;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// public class TcpSocketServerHandler {

// private volatile boolean running = true;

// @Autowired
// private TCPSocketSessionManager socketSessionManager;

// private ServerSocket serverSocket;

// private final ExecutorService executorService =
// Executors.newFixedThreadPool(10);

// @Autowired
// private TerminalService terminalService;

// @Async("taskExecutor")
// public void startServer(int port) {
// while (running) {
// try (ServerSocket serverSocket = new ServerSocket(port)) {
// this.serverSocket = serverSocket;
// log.info("Server started on port " + port);

// while (running && !serverSocket.isClosed()) {
// try {
// Socket clientSocket = serverSocket.accept();
// executorService.execute(() -> handleClientSocket(clientSocket));
// } catch (IOException e) {
// if (serverSocket.isClosed()) {
// log.info("Server socket closed, stopping server.");
// break;
// }
// log.error("Error accepting client connection", e);
// }
// }
// } catch (IOException e) {
// log.error("Failed to start server on port " + port + ", retrying in
// 5seconds...", e);
// try {
// Thread.sleep(5000);
// } catch (InterruptedException ie) {
// Thread.currentThread().interrupt();
// }
// }
// }
// executorService.shutdown();
// }

// public void stopServer() {
// running = false;

// if (serverSocket != null && !serverSocket.isClosed()) {
// try {
// serverSocket.close();
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// executorService.shutdown();
// }

// // @Async("taskExecutor")
// private void handleClientSocket(Socket clientSocket) {
// try {
// PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
// BufferedReader reader = new BufferedReader(new
// InputStreamReader(clientSocket.getInputStream()));
// String clientId = clientSocket.getInetAddress().toString();
// String inputLine;
// while ((inputLine = reader.readLine()) != null) {
// Map<String, Object> message = GenericUtil.convertJsonStringToMap(inputLine);
// Map<String, Object> outputMesage = new HashMap<String, Object>();
// if (message != null && message.containsKey("action") &&
// message.containsKey("serialNumber")) {
// String serialNumber = (String) message.get("serialNumber");

// if (serialNumber != null) {
// Terminal terminal = terminalService.getTerminalBySerialNumber(serialNumber);

// if (terminal != null) {
// String action = message.get("action").toString();
// if (Arrays.asList("joining", "heaartbeat").contains(action)) {
// if (action.equalsIgnoreCase("joining")) {
// socketSessionManager.removeSession(serialNumber);
// socketSessionManager.addSession(serialNumber, clientSocket);

// outputMesage.clear();
// outputMesage.put("action", "joined");
// outputMesage.put("message", "Connection received successfully");
// writer.println(GenericUtil.convertMapToJsonString(outputMesage));
// }
// terminal.setLastHeartbeat(LocalDateTime.now());
// terminal.setStatus(StatusType.ACTIVE);
// terminalService.getTerminalRepository().save(terminal);
// }
// } else {
// outputMesage.clear();
// outputMesage.put("action", "close");
// outputMesage.put("message", "Unauthorized Connection");
// writer.println(GenericUtil.convertMapToJsonString(outputMesage));
// clientSocket.close();
// }
// } else {
// outputMesage.clear();
// outputMesage.put("action", "close");
// outputMesage.put("message", "Unauthorized Connection. No Identifier");
// writer.println(GenericUtil.convertMapToJsonString(outputMesage));
// clientSocket.close();
// }

// }

// }

// log.info("Client disconnected: " + clientId);

// } catch (IOException e) {
// log.info("Socket exception: " + e.getMessage());
// } finally {
// try {
// clientSocket.close();
// } catch (IOException e) {
// log.info("Exception closing socket: " + e.getMessage());
// }
// }

// }

// public Map<String, Socket> getSocketMap() {
// return socketSessionManager.getSessions();
// }
// }
