package net.scapeemulator.xtalk.server.message.world;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.net.packet.*;
import net.scapeemulator.worldlist.World;

import io.netty.buffer.ByteBufAllocator;

public class XWorldRefreshMessage implements Message {
	public static final int OPCODE = 1;
	private final List<World> worlds;

	public XWorldRefreshMessage(List<World> worlds) {
		this.worlds = worlds;
	}

	public static class Encoder extends MessageEncoder<XWorldRefreshMessage> {

		public Encoder() {
			super(XWorldRefreshMessage.class);
		}

		@Override
		public Packet encode(ByteBufAllocator alloc, XWorldRefreshMessage message) throws IOException {
			PacketBuilder builder = new PacketBuilder(alloc, OPCODE, Packet.Type.VARIABLE_SHORT);
			Iterator<World> iter = message.worlds.iterator();
			int minId = message.worlds.size();
			while (iter.hasNext()) {
				World next = iter.next();
				int worldId = next.getId();
				if (worldId < minId)
					minId = worldId;
			}
			builder.putSmart(minId);
			builder.putSmart(message.worlds.size());
			iter = message.worlds.iterator();
			while (iter.hasNext()) {
				World next = iter.next();
				encode(builder, next, minId);
			}
			return builder.toPacket();
		}

		private void encode(PacketBuilder builder, World world, int minId) {
			builder.put(DataType.SHORT, world.getId() - minId);
			builder.put(DataType.BYTE, world.getCountry());
			builder.put(DataType.INT, world.getFlags());
			builder.putString(world.getActivity());
			builder.putString(world.getIp());
		}
	}
}
