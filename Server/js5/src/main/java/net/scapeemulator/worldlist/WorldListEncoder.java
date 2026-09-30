package net.scapeemulator.worldlist;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.scapeemulator.api.ByteBufUtils;

public final class WorldListEncoder extends MessageToByteEncoder<WorldListMessage> {

	@Override
	public void encode(ChannelHandlerContext ctx, WorldListMessage message, ByteBuf out) {
		try {
			ByteBuf buf = ctx.alloc().buffer();
			buf.writeByte(1);
			buf.writeByte(1);

			Map<Integer, Country> countries = message.getCountries();
			ByteBufUtils.writeSmart(buf, countries.size());
			for (Country country : countries.values()) {
				ByteBufUtils.writeSmart(buf, country.getFlag());
				ByteBufUtils.writeWorldListString(buf, country.getName());
			}

			World[] worlds = message.getWorlds();
			int minId = worlds[0].getId();
			int maxId = worlds[0].getId();
			for (int i = 1; i < worlds.length; i++) {
				World world = worlds[i];
				int id = world.getId();

				if (id > maxId)
					maxId = id;
				if (id < minId)
					minId = id;
			}

			ByteBufUtils.writeSmart(buf, minId);
			ByteBufUtils.writeSmart(buf, maxId);
			ByteBufUtils.writeSmart(buf, worlds.length);

			for (World world : worlds) {
				ByteBufUtils.writeSmart(buf, world.getId() - minId);
				buf.writeByte(world.getCountry());
				buf.writeInt(world.getFlags());
				ByteBufUtils.writeWorldListString(buf, world.getActivity());
				ByteBufUtils.writeWorldListString(buf, world.getIp());
			}

			buf.writeInt(0xDEADBEEF);

			for (int i = 0; i < worlds.length; i++) {
				World world = worlds[i];
				ByteBufUtils.writeSmart(buf, world.getId() - minId);
				if (world instanceof OfflineWorld) {
					buf.writeShort(-1);
				} else {
					buf.writeShort(((OnlineWorld) world).getPlayers());
				}
			}

			out.writeByte(0); // 0 = ok, 7/9 = world list full
			out.writeShort(buf.readableBytes());
			out.writeBytes(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
