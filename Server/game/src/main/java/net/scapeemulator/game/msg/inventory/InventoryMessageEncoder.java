package net.scapeemulator.game.msg.inventory;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.game.model.inventory.Item;

public final class InventoryMessageEncoder extends MessageEncoder<InventoryMessage> {

	public InventoryMessageEncoder() {
		super(InventoryMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InventoryMessage message) {
		Item[] items = message.getItems();

		PacketBuilder builder = new PacketBuilder(alloc, 11, Type.VARIABLE_SHORT);
		builder.put(DataType.SHORT, message.getType());
		builder.put(DataType.BYTE, 0);// ??
		builder.put(DataType.SHORT, items.length);
		for(int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item == null) {
				builder.put(DataType.BYTE, DataTransformation.SUBTRACT, 0);
				builder.put(DataType.SHORT, 0);
			} else {
				int amount = item.getAmount();
				if (amount >= 255) {
					builder.put(DataType.BYTE, DataTransformation.SUBTRACT, 255);
					builder.put(DataType.INT, DataOrder.LITTLE, amount);
				} else {
					builder.put(DataType.BYTE, DataTransformation.SUBTRACT, amount);
				}
				builder.put(DataType.SHORT, item.getId() + 1);
			}
		}
		return builder.toPacket();
	}
}
