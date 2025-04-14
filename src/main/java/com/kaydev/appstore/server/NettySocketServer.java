package com.kaydev.appstore.server;
// package com.iisysgroup.itexstore.server;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Component;

// import com.iisysgroup.itexstore.services.data.TerminalService;

// import io.netty.bootstrap.ServerBootstrap;
// import io.netty.channel.*;
// import io.netty.channel.nio.NioEventLoopGroup;
// import io.netty.channel.socket.SocketChannel;
// import io.netty.channel.socket.nio.NioServerSocketChannel;
// // import io.netty.handler.codec.LineBasedFrameDecoder;
// import io.netty.handler.codec.string.StringDecoder;
// import io.netty.handler.codec.string.StringEncoder;
// import io.netty.util.CharsetUtil;
// // import io.netty.handler.codec.string.StringDecoder;
// // import io.netty.handler.codec.string.StringEncoder;
// // import io.netty.handler.logging.LogLevel;
// // import io.netty.handler.logging.LoggingHandler;
// import jakarta.annotation.PostConstruct;
// import jakarta.annotation.PreDestroy;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Component
// public class NettySocketServer {

// @Autowired
// private TerminalService terminalService;

// @Value("${server.netty.port}")
// private int port;

// private final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
// private final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
// private ChannelFuture serverChannelFuture;

// @PostConstruct
// public void start() throws InterruptedException {
// ServerBootstrap bootstrap = new ServerBootstrap();
// bootstrap.group(bossGroup, workerGroup)
// .channel(NioServerSocketChannel.class)
// .childHandler(new ChannelInitializer<SocketChannel>() {
// @Override
// protected void initChannel(SocketChannel ch) {
// ChannelPipeline pipeline = ch.pipeline();
// // pipeline.addLast(new LineBasedFrameDecoder(16384));
// pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
// pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
// pipeline.addLast(new NettySocketServerHandler(terminalService));
// }
// });

// serverChannelFuture = bootstrap.bind(port).sync();
// serverChannelFuture.addListener(future -> {
// if (future.isSuccess()) {
// log.info("Socket server started on port " + port);
// } else {
// log.info("Failed to start socket server.");
// }
// });
// }

// @PreDestroy
// public void stop() throws InterruptedException {
// if (serverChannelFuture != null) {
// serverChannelFuture.channel().close().sync();
// }
// bossGroup.shutdownGracefully().sync();
// workerGroup.shutdownGracefully().sync();
// }
// }
