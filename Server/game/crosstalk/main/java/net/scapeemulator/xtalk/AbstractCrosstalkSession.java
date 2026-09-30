package net.scapeemulator.xtalk;

import java.io.IOException;

import io.netty.channel.Channel;
import net.scapeemulator.api.Session;

public abstract class AbstractCrosstalkSession<E extends AbstractCrosstalkClient> extends Session<E> {

	public AbstractCrosstalkSession(E service, Channel channel) {
		super(service, channel);
	}

	public Channel getChannel() {
		return channel;
	}

	@Override
	public abstract void messageReceived(Object message) throws IOException;
}
