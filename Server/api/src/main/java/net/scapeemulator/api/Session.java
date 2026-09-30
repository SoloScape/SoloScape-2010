package net.scapeemulator.api;

import java.io.IOException;

import io.netty.channel.Channel;

public abstract class Session<T extends Service> {
	protected final T service;
	protected Channel channel;

	public Session(T service, Channel channel) {
		this.service = service;
		this.channel = channel;

	}

	public final T getService() {
		return service;
	}

	public abstract void messageReceived(Object message) throws IOException;

	public void channelClosed() {
		/* empty */
	}
}
