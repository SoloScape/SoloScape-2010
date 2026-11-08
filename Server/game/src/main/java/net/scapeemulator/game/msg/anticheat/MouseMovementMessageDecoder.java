package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

public final class MouseMovementMessageDecoder extends MessageDecoder<MouseMovementMessage> {
	private static final MouseMovementMessage MESSAGE = new MouseMovementMessage();

	public MouseMovementMessageDecoder() {
		this(42);
	}

	public MouseMovementMessageDecoder(int opcode) {
		super(opcode);
	}

	@Override
	public MouseMovementMessage decode(Packet packet) {
		// Opcode 42 batches mouse movement; opcode 21 batches mouse clicks.
		// Neither is used for appearance changes or gameplay; discard telemetry.
		packet.getPayload().skipBytes(packet.getPayload().readableBytes());
		return MESSAGE;
	}
}
