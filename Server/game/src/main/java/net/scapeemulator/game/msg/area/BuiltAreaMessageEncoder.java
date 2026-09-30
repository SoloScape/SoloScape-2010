package net.scapeemulator.game.msg.area;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.game.util.LandscapeKeyTable;

public class BuiltAreaMessageEncoder extends MessageEncoder<BuiltAreaMessage> {
	// private final LandscapeKeyTable table;

	public BuiltAreaMessageEncoder(LandscapeKeyTable table) {
		super(BuiltAreaMessage.class);
		// this.table = table;
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, BuiltAreaMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 59, Type.VARIABLE_SHORT, 5000);
		builder.put(DataType.BYTE, 1);
		builder.put(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD, message.getPosition().getChunkY());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.isForcedRefresh() ? 1 : 0);
		builder.put(DataType.SHORT, DataTransformation.ADD, message.getPosition().getChunkX());
		builder.put(DataType.BYTE, DataTransformation.SUBTRACT, message.getFov().ordinal());
		int blocks = message.getFov().getChunks();
		// Chunk[][][] pallette = message.getPallette();
		// builder.switchToBitAccess();
		// for (int height = 0; height < 4; height++) {
		// for (int xChunk = 0; xChunk < blocks; xChunk++) {
		// for (int yChunk = 0; yChunk < blocks; yChunk++) {
		// if (height >= pallette.length || xChunk >= pallette[height].length
		// || yChunk >= pallette[height][xChunk].length) {
		// builder.putBit(false);
		// } else {
		// Chunk chunk = pallette[height][xChunk][yChunk];
		// builder.putBit(chunk != null);
		// if (chunk != null) {
		// builder.putBits(26, chunk.instruction());
		// }
		// }
		// }
		// }
		// }
		builder.switchToByteAccess();
		// List<Integer> sent = new ArrayList<>();
		for (int height = 0; height < 4; height++) {
			for (int xChunk = 0; xChunk < blocks; xChunk++) {
				for (int yChunk = 0; yChunk < blocks; yChunk++) {
					// if (height >= pallette.length || xChunk >=
					// pallette[height].length
					// || yChunk >= pallette[height][xChunk].length ||
					// pallette[height][xChunk][yChunk] == null) {
					// continue;
					// } else {
					// Chunk chunk = pallette[height][xChunk][yChunk];
					// int areaCode = chunk.getSource().toAreaInt() & 65535;
					// if (sent.contains(areaCode)) {
					// areaCode = -1;
					// }
					// if (areaCode != -1) {
					// sent.add(areaCode);
					// for (int key :
					// table.getKeys(chunk.getSource().getAreaX(),
					// chunk.getSource().getAreaY())) {
					// builder.put(DataType.INT, key);
					// }
					// }
					// }
				}
			}
		}
		return builder.toPacket();
	}
}
