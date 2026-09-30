package net.scapeemulator.game.msg.chunk;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.GameMapObject;

public class AddObjectMessage extends ChunkMessage {
	private final GameMapObject object;

	public AddObjectMessage(GameMapObject object) {
		super(12);
		this.object = object;
	}

	@Override
	protected void encodePacket(PacketBuilder pb) {
		pb.put(DataType.BYTE, DataTransformation.SUBTRACT, object.getTypRot());
		pb.put(DataType.SHORT, DataTransformation.ADD, object.getId());
		pb.put(DataType.BYTE, DataTransformation.ADD, object.getLoc());
	}
}
