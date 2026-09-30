package net.scapeemulator.game.msg.interf.child;

import io.netty.buffer.ByteBufAllocator;

import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.api.net.packet.Packet.Type;

public final class InterfaceTextMessageEncoder extends MessageEncoder<InterfaceTextMessage> {

	public InterfaceTextMessageEncoder() {
		super(InterfaceTextMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceTextMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 1, Type.VARIABLE_SHORT);
		builder.putString(message.getText());
		builder.put(DataType.SHORT, message.getSequenceNumber());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getId());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getSlot());
		return builder.toPacket();
	}
}
