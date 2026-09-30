package net.scapeemulator.xtalk.server.message.world;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.worldlist.OfflineWorld;
import net.scapeemulator.worldlist.OnlineWorld;
import net.scapeemulator.worldlist.World;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;

public class PlayerCountRefreshMessage implements Message {
	public static final int OPCODE = 4;
	private final int players;
	private final World[] worlds;

	public PlayerCountRefreshMessage(int players) {
		this.players = players;
		worlds = new World[0];
	}

	public int getPlayers() {
		return players;
	}

	public PlayerCountRefreshMessage(World[] worlds) {
		this.worlds = worlds;
		players = -1;
	}

	public static class Encoder extends MessageEncoder<PlayerCountRefreshMessage> {

		public Encoder() {
			super(PlayerCountRefreshMessage.class);
		}

		@Override
		public Packet encode(ByteBufAllocator alloc, PlayerCountRefreshMessage message) throws IOException {
			if (message.players != -1) {
				throw new IOException("Tried to encode a local playercount packet for the global world network!");
			} else {
				PacketBuilder pb = new PacketBuilder(alloc, OPCODE, Type.VARIABLE_SHORT);
				pb.put(DataType.BYTE, message.worlds.length);
				for (World world : message.worlds) {
					pb.put(DataType.SHORT, world.getId());
					if (world instanceof OfflineWorld) {
						pb.put(DataType.SHORT, -1);
					} else {
						pb.put(DataType.SHORT, ((OnlineWorld) world).getPlayers());
					}
				}
				return pb.toPacket();
			}
		}
	}

	public static class Decoder extends MessageDecoder<PlayerCountRefreshMessage> {

		public Decoder() {
			super(OPCODE);
		}

		@Override
		public PlayerCountRefreshMessage decode(Packet packet) throws IOException {
			return new PlayerCountRefreshMessage((int) new PacketReader(packet).getUnsigned(DataType.SHORT));
		}
	}

	public static class Handler extends MessageHandler<GameServerSession, PlayerCountRefreshMessage> {

		@Override
		public void handle(GameServerSession target, PlayerCountRefreshMessage message) {
			target.getWorld().setPlayers(message.getPlayers());
		}
	}
}
