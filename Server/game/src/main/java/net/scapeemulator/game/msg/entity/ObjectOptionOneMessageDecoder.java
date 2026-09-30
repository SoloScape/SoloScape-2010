package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.*;

public final class ObjectOptionOneMessageDecoder extends MessageDecoder<ObjectClickMessage> {

	public ObjectOptionOneMessageDecoder() {
		super(77);
	}

	@Override
	public ObjectClickMessage decode(Packet frame) {
		PacketReader reader = new PacketReader(frame);
		int x = (int) reader.getSigned(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD);
		boolean runSteps = reader.getUnsigned(DataType.BYTE) == 1;
		int id = (int) reader.getSigned(DataType.SHORT);
		int y = (int) reader.getSigned(DataType.SHORT);
		return new ObjectClickMessage(id, x, y, runSteps, 1);
	}
}
