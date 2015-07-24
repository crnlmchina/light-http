package com.dangdang.light.http.netty;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.light.http.IRequestPathDispather;
import com.dangdang.light.http.exception.ExceptionLoggerContext;
import com.dangdang.light.http.request.LightHttpRequest;
import com.dangdang.light.http.util.NettyRequestUtil;
import com.google.common.base.Stopwatch;

/**
 * 请求处理类
 * <p>
 * Not thread-safe
 * </p>
 * 
 * @author wangyuxuan
 * 
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

	private IRequestPathDispather pathDispather;

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);

	public HttpServerHandler(IRequestPathDispather pathDispather) {
		super();
		this.pathDispather = pathDispather;
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
		saveClientIpToContext(ctx);

		if (!request.getDecoderResult().isSuccess()) {
			sendError(ctx, BAD_REQUEST);
			return;
		}

		writeResponse(request, ctx);
	}

	/**
	 * Save client ip to context
	 * 
	 * @param ctx
	 */
	private void saveClientIpToContext(ChannelHandlerContext ctx) {
		final SocketAddress remoteAddress = ctx.channel().remoteAddress();
		if (remoteAddress instanceof InetSocketAddress) {
			final InetSocketAddress socketAddress = (InetSocketAddress) remoteAddress;
			final InetAddress inetaddress = socketAddress.getAddress();
			ChannelContext.setClientIp(inetaddress.getHostAddress());
		}
	}

	private void writeResponse(HttpRequest request, ChannelHandlerContext ctx) {
		// Build the response object.
		FullHttpResponse response = null;
		try {
			String body = executeQuery(request) + System.lineSeparator();

			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.copiedBuffer(body, CharsetUtil.UTF_8));
			response.headers().set(CONTENT_TYPE, this.pathDispather.getContentType());
			response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		} catch (Throwable e) {
			ExceptionLoggerContext.getExceptionLogger().log("Handler logic error.", e);
			sendError(ctx, INTERNAL_SERVER_ERROR);
			return;
		}

		// Decide whether to close the connection or not.
		boolean keepAlive = isKeepAlive(request);
		if (keepAlive) {
			response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
		} else {
			response.headers().set(CONNECTION, HttpHeaders.Values.CLOSE);
		}

		ChannelFuture future = ctx.writeAndFlush(response);
		if (!keepAlive) {
			future.addListener(ChannelFutureListener.CLOSE);
		}
	}

	private String executeQuery(HttpRequest request) {
		LightHttpRequest lightRequest = NettyRequestUtil.toLightRequest(request);
		String requestPath = lightRequest.getPath();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[{}][request] {}", requestPath, lightRequest);
		}

		Stopwatch stopwatch = Stopwatch.createStarted();
		String response = this.pathDispather.dispatch(lightRequest);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("[{}][response] {}", requestPath, response);

		stopwatch.stop();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[{}][Use time] {} ms.", requestPath, stopwatch.elapsed(TimeUnit.MILLISECONDS));
		}
		return response;
	}

	private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString(),
				CharsetUtil.UTF_8));
		response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ExceptionLoggerContext.getExceptionLogger().log("Error ocurr.", cause);
		ctx.close();
	}

}
