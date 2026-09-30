package net.scapeemulator.game.msg.area;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;

import java.io.IOException;

public final class WalkMessageDecoder extends MessageDecoder<WalkMessage> {

	public WalkMessageDecoder(int opcode) {
		super(opcode);
	}

	@Override
	public WalkMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);

		boolean running = reader.getUnsigned(DataType.BYTE) == 1;
		int x = (int) reader.getUnsigned(DataType.SHORT);
		int y = (int) reader.getUnsigned(DataType.SHORT);
		return new WalkMessage(x, y, running, frame.getOpcode() == WalkMessage.MINIMAP_WALK);
	}
}
