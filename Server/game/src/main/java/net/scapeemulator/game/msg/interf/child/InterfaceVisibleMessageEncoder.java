package net.scapeemulator.game.msg.interf.child;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public final class InterfaceVisibleMessageEncoder extends MessageEncoder<InterfaceVisibleMessage> {

	public InterfaceVisibleMessageEncoder() {
		super(InterfaceVisibleMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceVisibleMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 21);
		builder.put(DataType.BYTE, DataTransformation.NEGATE, message.isVisible() ? 0 : 1);
		builder.put(DataType.SHORT, 0);
		builder.put(DataType.INT, DataOrder.LITTLE, (message.getId() << 16) | message.getSlot());
		return builder.toPacket();
	}

}
