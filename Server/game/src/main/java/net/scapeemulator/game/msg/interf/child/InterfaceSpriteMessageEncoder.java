package net.scapeemulator.game.msg.interf.child;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class InterfaceSpriteMessageEncoder extends MessageEncoder<InterfaceSpriteMessage> {

	public InterfaceSpriteMessageEncoder() {
		super(InterfaceSpriteMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceSpriteMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 5);
		builder.put(DataType.SHORT, message.getSequenceNumber());
		builder.put(DataType.INT, DataOrder.INVERSED_MIDDLE, message.getPointer());
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getSpriteId());
		return builder.toPacket();
	}
}
