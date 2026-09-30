package net.scapeemulator.game.msg.chunk;

import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;

public abstract class ChunkMessage {
	private final int opcode;

	public ChunkMessage(int opcode) {
		this.opcode = opcode;
	}

	public final void encode(PacketBuilder pb) {
		pb.put(DataType.BYTE, opcode);
		encodePacket(pb);
	}

	protected abstract void encodePacket(PacketBuilder pb);
}
