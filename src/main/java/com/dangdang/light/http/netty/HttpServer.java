package com.dangdang.light.http.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.light.http.exception.ExceptionLogger;
import com.dangdang.light.http.exception.ExceptionLoggerContext;
import com.dangdang.light.http.exception.SimpleExceptionLogger;
import com.google.common.base.Preconditions;

/**
 * Http服务启动器
 * 
 * @author wangyuxuan
 * 
 */
public class HttpServer {

	private final ChannelInitializer<SocketChannel> channelInitializer;

	private final int port;

	private final int tcpSoBacklog;

	public HttpServer(ChannelInitializer<SocketChannel> channelInitializer, int port) {
		this(channelInitializer, port, 256);
	}

	public HttpServer(ChannelInitializer<SocketChannel> channelInitializer, int port, int tcpSoBacklog) {
		super();
		this.channelInitializer = Preconditions.checkNotNull(channelInitializer);
		this.port = port;
		this.tcpSoBacklog = tcpSoBacklog;
	}

	public void setExceptionLogger(ExceptionLogger exceptionLogger) {
		ExceptionLoggerContext.register(exceptionLogger);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

	public void run() throws Exception {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Server starting up at port: " + this.port);
		}
		// Initialize exception logger
		initExceptionLogger();
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, tcpSoBacklog);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(channelInitializer);

			Channel ch = b.bind(this.port).sync().channel();

			ch.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	/**
	 * Initialize exception logger
	 */
	private void initExceptionLogger() {
		if (ExceptionLoggerContext.getExceptionLogger() == null) {
			ExceptionLoggerContext.register(new SimpleExceptionLogger());
		}
	}
}
