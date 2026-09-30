package net.scapeemulator.api.message.codec;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.net.packet.Packet;

import java.io.IOException;

public abstract class MessageDecoder<T extends Message> {

	protected final int opcode;

	public MessageDecoder(int opcode) {
		this.opcode = opcode;
	}

	public abstract T decode(Packet packet) throws IOException;

}
