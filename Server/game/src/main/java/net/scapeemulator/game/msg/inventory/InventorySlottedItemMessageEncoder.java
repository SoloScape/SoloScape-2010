package net.scapeemulator.game.msg.inventory;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.inventory.SlottedItem;

public final class InventorySlottedItemMessageEncoder extends MessageEncoder<InventorySlottedItemMessage> {

	public InventorySlottedItemMessageEncoder() {
		super(InventorySlottedItemMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, InventorySlottedItemMessage message) {
		SlottedItem[] items = message.getItems();

		PacketBuilder builder = new PacketBuilder(alloc, 32, Type.VARIABLE_SHORT);
		
		builder.put(DataType.SHORT, message.getType());
		builder.put(DataType.BYTE, 0);// Flags

		for (SlottedItem slottedItem : items) {
			builder.putSmart(slottedItem.getSlot());

			Item item = slottedItem.getItem();
			if (item == null) {
				builder.put(DataType.SHORT, 0);
			} else {
				int amount = item.getAmount();
				builder.put(DataType.SHORT, item.getId() + 1);
				if (amount >= 255) {
					builder.put(DataType.BYTE, 255);
					builder.put(DataType.INT, amount);
				} else {
					builder.put(DataType.BYTE, amount);
				}
			}
		}

		return builder.toPacket();
	}

}
