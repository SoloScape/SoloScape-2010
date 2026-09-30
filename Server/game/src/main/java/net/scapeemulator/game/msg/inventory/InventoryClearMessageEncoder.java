package net.scapeemulator.game.msg.inventory;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

import java.io.IOException;

public final class InventoryClearMessageEncoder extends MessageEncoder<InventoryClearMessage> {

	public InventoryClearMessageEncoder() {
		super(InventoryClearMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InventoryClearMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 50);
		builder.put(DataType.SHORT, message.getType());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, 0);
		return builder.toPacket();
	}

}
