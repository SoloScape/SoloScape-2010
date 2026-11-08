package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

public final class LatencyMessageDecoder extends MessageDecoder<PingMessage> {
	private static final PingMessage MESSAGE = new PingMessage();

	public LatencyMessageDecoder() {
		super(19);
	}

	@Override
	public PingMessage decode(Packet packet) {
		// The 592 client reports its measured ping as a two-byte value.
		// Latency statistics are unused; accept it through the existing ping handler.
		packet.getPayload().readUnsignedShort();
		return MESSAGE;
	}
}
