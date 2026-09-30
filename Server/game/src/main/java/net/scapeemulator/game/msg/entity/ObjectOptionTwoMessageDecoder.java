package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.*;

public final class ObjectOptionTwoMessageDecoder extends MessageDecoder<ObjectClickMessage> {

	public ObjectOptionTwoMessageDecoder() {
		super(11);
	}

	@Override
	public ObjectClickMessage decode(Packet frame) {
		PacketReader reader = new PacketReader(frame);
		boolean runSteps = reader.getUnsigned(DataType.BYTE) == 1;
		int y = (int) reader.getSigned(DataType.SHORT, DataOrder.LITTLE);
		int id = (int) reader.getSigned(DataType.SHORT, DataTransformation.ADD);
		int x = (int) reader.getSigned(DataType.SHORT);
		return new ObjectClickMessage(id, x, y, runSteps, 2);
	}

}
