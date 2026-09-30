package net.scapeemulator.game.msg.interf;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.*;

public class InterfaceMoveMessageEncoder extends MessageEncoder<InterfaceMoveMessage> {

	public InterfaceMoveMessageEncoder() {
		super(InterfaceMoveMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InterfaceMoveMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 106);
		builder.put(DataType.INT, DataOrder.LITTLE, message.getTarget());
		builder.put(DataType.INT, DataOrder.INVERSED_MIDDLE, message.getSource());
		builder.put(DataType.SHORT, DataOrder.LITTLE, message.getSequenceNumber());
		return builder.toPacket();
	}
}
