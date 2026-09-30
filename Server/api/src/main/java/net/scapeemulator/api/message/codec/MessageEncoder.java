package net.scapeemulator.api.message.codec;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.net.packet.Packet;

import java.io.IOException;

public abstract class MessageEncoder<T extends Message> {

	protected final Class<T> clazz;

	public MessageEncoder(Class<T> clazz) {
		this.clazz = clazz;
	}

	public abstract Packet encode(ByteBufAllocator alloc, T message) throws IOException;

}
