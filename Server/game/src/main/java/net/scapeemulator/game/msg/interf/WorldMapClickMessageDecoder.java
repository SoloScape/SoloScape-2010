package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.*;

public final class WorldMapClickMessageDecoder extends MessageDecoder<WorldMapClickMessage> {
	public WorldMapClickMessageDecoder() {
		super(57);
	}

	@Override
	public WorldMapClickMessage decode(Packet packet) {
		// Build 592 writes the coordinate's bytes in order 1, 0, 3, 2.
		return new WorldMapClickMessage((int) new PacketReader(packet).getSigned(DataType.INT, DataOrder.MIDDLE));
	}
}
