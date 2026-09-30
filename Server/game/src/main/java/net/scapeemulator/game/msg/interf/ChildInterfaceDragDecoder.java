package net.scapeemulator.game.msg.interf;

import java.io.IOException;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceDragAction;

public class ChildInterfaceDragDecoder extends MessageDecoder<ChildInterfaceDragAction> {

	public ChildInterfaceDragDecoder() {
		super(75);
	}

	@Override
	public ChildInterfaceDragAction decode(Packet packet) throws IOException {
		PacketReader reader = new PacketReader(packet);
		int pointerFrom = (int) reader.getSigned(DataType.INT);
		int fromId = (pointerFrom >> 16) & 0xFFFF;
		int fromChild = pointerFrom & 0xFFFF;
		int slotFrom = (int) reader.getSigned(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD);
		//
		int pointerTo = (int) reader.getSigned(DataType.INT, DataOrder.MIDDLE);
		int toId = (pointerTo >> 16) & 0xFFFF;
		int toChild = pointerTo & 0xFFFF;
		int targetSlot = (int) reader.getSigned(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD);
		int srcMedia = (int) reader.getSigned(DataType.SHORT, DataOrder.LITTLE);
		int targetMedia = (int) reader.getSigned(DataType.SHORT, DataOrder.LITTLE);
		if (srcMedia == 65535)
			srcMedia = -1;
		if (targetMedia == 65535)
			targetMedia = -1;
		return new ChildInterfaceDragAction(fromId, fromChild, slotFrom, srcMedia, toId, toChild, targetSlot,
				targetMedia);
	}
}
