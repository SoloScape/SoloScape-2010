package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.*;

public final class NpcOptionTwoMessageDecoder extends MessageDecoder<NpcOptionTwoMessage> {
	public NpcOptionTwoMessageDecoder() {
		super(35);
	}

	@Override
	public NpcOptionTwoMessage decode(Packet packet) {
		PacketReader reader = new PacketReader(packet);
		// Build 592: byte-subtract Ctrl/run flag, then a big-endian NPC index.
		boolean running = reader.getUnsigned(DataType.BYTE, DataTransformation.SUBTRACT) == 1;
		return new NpcOptionTwoMessage((int) reader.getUnsigned(DataType.SHORT), running);
	}
}
