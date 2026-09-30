package net.scapeemulator.api.message.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.api.message.Message;

import java.util.HashMap;
import java.util.Map;

public final class MessageDispatcher<E extends Object> {

	private static final Logger logger = LoggerFactory.getLogger(MessageDispatcher.class);

	private final Map<Class<?>, MessageHandler<E, ?>> handlers = new HashMap<>();

	public <T extends Message> void bind(Class<T> clazz, MessageHandler<E, T> handler) {
		handlers.put(clazz, handler);
	}

	@SuppressWarnings("unchecked")
	public void dispatch(E src, Message message) {
		MessageHandler<E, Message> handler = (MessageHandler<E, Message>) handlers.get(message.getClass());
		if (handler != null) {
			try {
				handler.handle(src, message);
			} catch (Throwable t) {
				logger.warn("Error processing packet.", t);
			}
		} else {
			logger.warn("Cannot dispatch message (no handler): " + message.getClass().getName() + ".");
		}
	}
}
