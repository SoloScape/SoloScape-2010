package net.scapeemulator.game.msg.interf.child;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

public class PlayerOnInterfaceMessageEncoder extends MessageEncoder<PlayerOnInterfaceMessage> {

	public PlayerOnInterfaceMessageEncoder() {
		super(PlayerOnInterfaceMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, PlayerOnInterfaceMessage message) throws IOException {
		PacketBuilder builder;
		if (message.getPlayerId() == -1) {
			builder = new PacketBuilder(alloc, 103);
			builder.put(DataType.INT, message.getPointer());
			builder.put(DataType.SHORT, DataTransformation.ADD, message.getSequenceNumber());
		} else {
			builder = new PacketBuilder(alloc, 20);
			builder.put(DataType.INT, DataOrder.MIDDLE, message.getPointer());
			builder.put(DataType.INT, message.getType());
			builder.put(DataType.SHORT, message.getSequenceNumber());
			builder.put(DataType.SHORT, DataTransformation.ADD, message.getPlayerId());
		}
		return builder.toPacket();
	}
}
