package net.scapeemulator.worldlist;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import net.scapeemulator.api.Session;

public final class WorldListSession extends Session<WorldListService> {

	public WorldListSession(WorldListService service, Channel channel) {
		super(service, channel);
	}

	@Override
	public void messageReceived(Object message) {
		channel.write(new WorldListMessage(service.getCountries(), service.getWorlds())).addListener(ChannelFutureListener.CLOSE);
	}
}
