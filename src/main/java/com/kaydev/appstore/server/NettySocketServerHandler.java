package com.kaydev.appstore.server;
// package com.iisysgroup.itexstore.server;

// import io.netty.channel.Channel;
// import io.netty.channel.ChannelHandlerContext;
// import io.netty.channel.ChannelInboundHandlerAdapter;
// import io.netty.channel.group.ChannelGroup;
// import io.netty.channel.group.DefaultChannelGroup;
// import io.netty.util.concurrent.GlobalEventExecutor;
// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import com.iisysgroup.itexstore.models.entities.Terminal;
// import com.iisysgroup.itexstore.models.enums.StatusType;
// import com.iisysgroup.itexstore.services.data.TerminalService;
// import com.iisysgroup.itexstore.utils.GenericUtil;

// import java.time.LocalDateTime;
// import java.util.Arrays;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.concurrent.ConcurrentHashMap;

// @Slf4j
// @AllArgsConstructor
// public class NettySocketServerHandler extends ChannelInboundHandlerAdapter {

// private final TerminalService terminalService;

// private static final ChannelGroup channels = new
// DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
// private static final Map<String, Channel> terminalChannels = new
// ConcurrentHashMap<>();

// @Override
// public void channelActive(ChannelHandlerContext ctx) {
// Channel incoming = ctx.channel();
// channels.add(incoming);
// }

// @Override
// public void channelRead(ChannelHandlerContext ctx, Object msg) {
// try {
// Map<String, Object> responseMessage = new HashMap<>();
// Map<String, Object> receivedMessage =
// GenericUtil.convertJsonStringToMap(msg.toString());

// if (receivedMessage != null && receivedMessage.containsKey("action")
// && receivedMessage.containsKey("serialNumber")) {

// String serialNumber = (String) receivedMessage.get("serialNumber");

// if (serialNumber != null) {
// Terminal terminal = terminalService.getTerminalBySerialNumber(serialNumber);
// if (terminal != null) {
// // log.info("Connsection received from : " + terminal.getSerialNumber());
// String action = receivedMessage.get("action").toString();
// if (Arrays.asList("joining", "heartbeat").contains(action)) {
// if (action.equalsIgnoreCase("joining")) {
// terminalChannels.put(serialNumber, ctx.channel());

// responseMessage.put("action", "joined");
// responseMessage.put("message", "Connection received successfully");

// ctx.writeAndFlush(receivedMessage);
// ctx.writeAndFlush((Object)
// GenericUtil.convertMapToJsonString(responseMessage));
// }
// terminal.setLastHeartbeat(LocalDateTime.now());
// terminal.setStatus(StatusType.ACTIVE);
// terminalService.getTerminalRepository().save(terminal);
// }
// } else {
// // log.info("No terrminal found Unauthorized Connection from : " +
// // serialNumber);
// responseMessage.put("action", "close");
// responseMessage.put("message", "Unauthorized Connection");
// ctx.writeAndFlush((Object)
// GenericUtil.convertMapToJsonString(responseMessage));
// // ctx.close();
// }
// } else {
// // log.info("Unauthorized Connection from : " +
// ctx.channel().remoteAddress());
// responseMessage.put("action", "close");
// responseMessage.put("message", "Unauthorized Connection. No Identifier");
// ctx.writeAndFlush((Object)
// GenericUtil.convertMapToJsonString(responseMessage));
// // ctx.close();
// }
// }
// } catch (Exception e) {
// log.error("error", e);
// }

// }

// @Override
// public void channelInactive(ChannelHandlerContext ctx) {
// Channel incoming = ctx.channel();
// channels.remove(incoming);
// terminalChannels.values().remove(incoming);
// }

// @Override
// public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
// log.error(
// "diconnection from : " + ctx.channel().remoteAddress() + " : " +
// cause.getMessage());
// ctx.close();
// }

// public static Channel getSocketBySN(String serialNumber) {
// return terminalChannels.get(serialNumber);
// }

// public static void removeSocketBySN(String serialNumber) {
// Channel channel = terminalChannels.remove(serialNumber);
// if (channel != null) {
// channel.close();
// }
// }

// public static ChannelGroup getTerminalChannels() {
// return channels;
// }
// }
