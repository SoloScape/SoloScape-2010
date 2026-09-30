package net.scapeemulator.xtalk.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import net.scapeemulator.api.Session;
import net.scapeemulator.js5.UpdateSession;
import net.scapeemulator.worldlist.WorldListSession;
import net.scapeemulator.xtalk.CrosstalkServer;
import net.scapeemulator.xtalk.net.autoworld.AutoWorldSession;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;
import net.scapeemulator.xtalk.net.handshake.HandshakeMessage;
import net.scapeemulator.xtalk.net.login.WorldLoginSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CrosstalkChannelHandler extends ChannelInboundMessageHandlerAdapter<Object> {
	private static final Logger logger = LoggerFactory.getLogger(CrosstalkChannelHandler.class);
	private final CrosstalkServer server;
	private Session<?> session;

	public CrosstalkChannelHandler(CrosstalkServer server) {
		this.server = server;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		logger.info("Channel connected: " + ctx.channel().remoteAddress() + ".");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		logger.info("Channel disconnected: " + ctx.channel().remoteAddress() + ".");
		if (session != null)
			session.channelClosed();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Object message) throws Exception {
		if (session != null) {
			session.messageReceived(message);
		} else {
			HandshakeMessage handshake = (HandshakeMessage) message;

			switch (handshake.getService()) {
			case HandshakeMessage.SERVICE_UPDATE:
				session = new UpdateSession(server.getJs5(), ctx.channel());
				break;
			case HandshakeMessage.SERVICE_WORLD_LIST:
				session = new WorldListSession(server.getServerService(), ctx.channel());
				break;
			case HandshakeMessage.SERVICE_WORLD_LOGIN:
				session = new WorldLoginSession(server, ctx.channel());
				break;
			case HandshakeMessage.SERVICE_AUTO_LOGIN:
				session = new AutoWorldSession(server.getServerService(), ctx.channel());
				break;
			}
		}
	}

	public void setSession(GameServerSession session) {
		this.session = session;
	}
}
