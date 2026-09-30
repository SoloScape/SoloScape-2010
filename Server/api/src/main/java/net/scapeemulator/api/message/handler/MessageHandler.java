package net.scapeemulator.api.message.handler;

import net.scapeemulator.api.message.Message;

public abstract class MessageHandler<E, T extends Message> {

	public abstract void handle(E target, T message);

}
