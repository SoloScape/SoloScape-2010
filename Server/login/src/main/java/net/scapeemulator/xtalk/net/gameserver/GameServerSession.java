package net.scapeemulator.xtalk.net.gameserver;

import java.io.IOException;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import net.scapeemulator.api.Session;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.xtalk.server.GameServerService;
import net.scapeemulator.xtalk.server.XTalkWorld;
import net.scapeemulator.xtalk.server.message.world.XPingMessage;

public class GameServerSession extends Session<GameServerService> {
	private final MessageDispatcher<GameServerSession> dispatcher;
	private long lastKnownMessage = System.currentTimeMillis();
	private XTalkWorld world;

	public GameServerSession(GameServerService service, Channel channel) {
		super(service, channel);
		dispatcher = service.getDispatcher();
	}

	public ChannelFuture send(Message message) {
		lastKnownMessage = System.currentTimeMillis();
		return channel.write(message);
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		dispatcher.dispatch(this, (Message) message);
	}

	@Override
	public void channelClosed() {
		if (world != null)
			service.logout(world);
	}

	public Channel getChannel() {
		return channel;
	}

	public void checkAndPing() {
		long time = System.currentTimeMillis();
		if ((time - lastKnownMessage) > 1000) {
			send(new XPingMessage());
		}
	}

	public XTalkWorld getWorld() {
		return world;
	}

	public void setWorld(XTalkWorld world) {
		this.world = world;
	}
}
