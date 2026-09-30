package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

import java.io.IOException;

public final class PingMessageDecoder extends MessageDecoder<PingMessage> {

	private static final PingMessage PING_MESSAGE = new PingMessage();

	public PingMessageDecoder() {
		super(74);
	}

	@Override
	public PingMessage decode(Packet frame) throws IOException {
		return PING_MESSAGE;
	}
}
