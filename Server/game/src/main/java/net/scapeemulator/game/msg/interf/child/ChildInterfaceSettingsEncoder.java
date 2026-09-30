package net.scapeemulator.game.msg.interf.child;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class ChildInterfaceSettingsEncoder extends MessageEncoder<ChildInterfaceSettings> {

	public ChildInterfaceSettingsEncoder() {
		super(ChildInterfaceSettings.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, ChildInterfaceSettings message) throws IOException {
		// TODO: Small settings?
		PacketBuilder builder = new PacketBuilder(alloc, 75);
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, message.getStart());
		builder.put(DataType.INT, DataOrder.INVERSED_MIDDLE, message.getSetting());
		builder.put(DataType.SHORT, message.getSequenceNumber());// seq number
		builder.put(DataType.INT, (message.getId() << 16) | message.getChildId());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getEnd());
		return builder.toPacket();
	}
}
