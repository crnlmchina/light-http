package com.dangdang.light.http.netty;

import javax.annotation.PreDestroy;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import com.dangdang.light.http.IRequestPathDispather;
import com.google.common.base.Preconditions;

/**
 * 请求初始化配置
 * 
 * @author wangyuxuan
 * 
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

	private EventExecutorGroup group;

	private IRequestPathDispather pathDispather;

	private int readerIdleTimeSeconds;
	private int writerIdleTimeSeconds;

	public HttpServerInitializer(int threads, IRequestPathDispather pathDispather) {
		this(threads, pathDispather, 10, 10);
	}

	public HttpServerInitializer(int threads, IRequestPathDispather pathDispather, int readerIdleTimeSeconds, int writerIdleTimeSeconds) {
		super();
		Preconditions.checkArgument(threads > 0);
		this.group = new DefaultEventExecutorGroup(threads);
		this.pathDispather = Preconditions.checkNotNull(pathDispather);
		this.readerIdleTimeSeconds = readerIdleTimeSeconds;
		this.writerIdleTimeSeconds = writerIdleTimeSeconds;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline p = ch.pipeline();
		p.addLast("idleStateHandler", new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, 0));
		p.addLast("myHandler", new IdleHandler());

		p.addLast("http-decoder", new HttpRequestDecoder());
		p.addLast("http-encoder", new HttpResponseEncoder());

		p.addLast("http-aggregator", new HttpObjectAggregator(Integer.MAX_VALUE));

		p.addLast(group, new HttpServerHandler(this.pathDispather));
	}

	@PreDestroy
	private void destroy() {
		if (group != null)
			group.shutdownGracefully();
	}

	// Handler should handle the IdleStateEvent triggered by IdleStateHandler.
	public class IdleHandler extends ChannelDuplexHandler {
		@Override
		public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
			if (evt instanceof IdleStateEvent) {
				IdleStateEvent e = (IdleStateEvent) evt;
				if (e.state() == IdleState.READER_IDLE) {
					ctx.close();
				} else if (e.state() == IdleState.WRITER_IDLE) {
					ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
				}
			}
		}
	}
}
