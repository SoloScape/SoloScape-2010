package net.scapeemulator.xtalk.net.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.xtalk.client.CrosstalkClient;
import net.scapeemulator.api.net.packet.*;

public class PlayerCountRefreshMessage implements Message {
	public static final int OPCODE = 4;
	private final int players;
	private Map<Integer, Integer> plrCounts;

	public PlayerCountRefreshMessage(int players) {
		this.players = players;
	}

	public PlayerCountRefreshMessage(Map<Integer, Integer> plrCounts) {
		this.plrCounts = plrCounts;
		players = -1;
	}

	public Map<Integer, Integer> getPlrCounts() {
		return plrCounts;
	}

	public static class Encoder extends MessageEncoder<PlayerCountRefreshMessage> {

		public Encoder() {
			super(PlayerCountRefreshMessage.class);
		}

		@Override
		public Packet encode(ByteBufAllocator alloc, PlayerCountRefreshMessage message) throws IOException {
			PacketBuilder pb = new PacketBuilder(alloc, OPCODE);
			pb.put(DataType.SHORT, message.players);
			return pb.toPacket();
		}
	}

	public static class Decoder extends MessageDecoder<PlayerCountRefreshMessage> {

		public Decoder() {
			super(OPCODE);
		}

		@Override
		public PlayerCountRefreshMessage decode(Packet packet) throws IOException {
			PacketReader reader = new PacketReader(packet);
			int worldCount = (int) reader.getUnsigned(DataType.BYTE);
			Map<Integer, Integer> plrCounts = new HashMap<>();
			for (int i = 0; i < worldCount; i++) {
				int worldId = (int) reader.getUnsigned(DataType.SHORT);
				int players = (int) reader.getUnsigned(DataType.SHORT);
				plrCounts.put(worldId, players);
			}
			return new PlayerCountRefreshMessage(plrCounts);
		}
	}

	public static class Handler extends MessageHandler<CrosstalkClient, PlayerCountRefreshMessage> {

		@Override
		public void handle(CrosstalkClient target, PlayerCountRefreshMessage message) {
			Map<Integer, Integer> counts = message.getPlrCounts();
			for (Entry<Integer, Integer> entry : counts.entrySet()) {
				target.getGameServer().getWorldListService().setPlayers(entry.getKey(), entry.getValue());
			}
		}
	}
}
