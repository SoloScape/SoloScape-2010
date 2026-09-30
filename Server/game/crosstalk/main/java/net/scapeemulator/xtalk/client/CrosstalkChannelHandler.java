package net.scapeemulator.xtalk.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;

public class CrosstalkChannelHandler extends ChannelInboundMessageHandlerAdapter<Object> {
	private CrosstalkClient client;

	public CrosstalkChannelHandler(CrosstalkClient client) {
		this.client = client;
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		if (client != null)
			client.channelClosed();

		// logger.info("Channel disconnected: " + ctx.channel().remoteAddress()
		// + ".");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// logger.warn("Exception caught, closing channel...", cause);
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object message) throws Exception {
		if (client.getSession() != null)
			client.getSession().messageReceived(message);
	}

	public void setSession(CrosstalkSession session) {
		client.setSession(session);
	}
}
