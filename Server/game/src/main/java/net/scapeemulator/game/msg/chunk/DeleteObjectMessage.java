package net.scapeemulator.game.msg.chunk;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.GameMapObject;

public class DeleteObjectMessage extends ChunkMessage {
	private GameMapObject object;

	public DeleteObjectMessage(GameMapObject object) {
		super(9);
		this.object = object;
	}

	@Override
	protected void encodePacket(PacketBuilder pb) {
		pb.put(DataType.BYTE, DataTransformation.ADD, object.getTypRot());
		pb.put(DataType.BYTE, DataTransformation.NEGATE, object.getLoc());
	}
}
