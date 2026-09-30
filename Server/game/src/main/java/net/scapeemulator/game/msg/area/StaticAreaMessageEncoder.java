package net.scapeemulator.game.msg.area;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.util.LandscapeKeyTable;

public final class StaticAreaMessageEncoder extends MessageEncoder<StaticAreaMessage> {

	private final LandscapeKeyTable table;

	public StaticAreaMessageEncoder(LandscapeKeyTable table) {
		super(StaticAreaMessage.class);
		this.table = table;
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, StaticAreaMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 13, Type.VARIABLE_SHORT);
		Position position = message.getPosition();
		if (message.getPlayerId() != -1) {
			builder = new PacketBuilder(alloc, 13, Type.VARIABLE_SHORT, 5000);
			builder.switchToBitAccess();
			builder.putBits(30, position.toPackedInt());
			for (int index = 1; index < 2048; index++) {
				if (index != message.getPlayerId()) {
					int loc = 0;
					if (message.getPositionBlock().containsKey(index)) {
						loc = message.getPositionBlock().get(index).toAreaInt();
					}
					builder.putBits(18, loc);
				}
			}
			builder.switchToByteAccess();
		}
		final int cx = position.getChunkX(), cy = position.getChunkY();
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getFov().ordinal());
		builder.put(DataType.SHORT, DataTransformation.ADD, cx);
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, cy);
		builder.put(DataType.BYTE, DataTransformation.ADD, message.forcedRefresh() ? 1 : 0);
		int distance = message.getFov().getTiles() >> 4;
		for (int mapX = ((cx - distance) / 8); mapX <= ((cx + distance) / 8); mapX++) {
			for (int mapY = ((cy - distance) / 8); mapY <= ((cy + distance) / 8); mapY++) {
				int[] keys = table.getKeys(mapX, mapY);
				for (int i = 0; i < 4; i++) {
					builder.put(DataType.INT, keys[i]);
				}
			}
		}
		return builder.toPacket();
	}
}
