package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

public final class MouseMovementMessageDecoder extends MessageDecoder<MouseMovementMessage> {
	private static final MouseMovementMessage MESSAGE = new MouseMovementMessage();

	public MouseMovementMessageDecoder() {
		super(42);
	}

	@Override
	public MouseMovementMessage decode(Packet packet) {
		// The 592 client batches compressed mouse coordinates and timing deltas.
		// No mouse-telemetry analysis is implemented, so consume the framed batch.
		packet.getPayload().skipBytes(packet.getPayload().readableBytes());
		return MESSAGE;
	}
}
