package net.scapeemulator.xtalk.client;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.xtalk.AbstractCrosstalkSession;

public class CrosstalkSession extends AbstractCrosstalkSession<CrosstalkClient> {
	private final MessageDispatcher<CrosstalkClient> dispatcher;
	private final Queue<Message> messages = new ArrayDeque<>();

	public CrosstalkSession(CrosstalkClient service, Channel channel) {
		super(service, channel);
		dispatcher = service.getDispatcher();
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		messages.add((Message) message);
	}

	@Override
	public void channelClosed() {
		// TODO Auto-generated method stub
	}

	public ChannelFuture send(Message message) {
		return channel.write(message);
	}

	public void processMessages() {
		synchronized (messages) {
			Message message;
			while ((message = messages.poll()) != null) {
				dispatcher.dispatch(service, message);
			}
		}
	}
}
