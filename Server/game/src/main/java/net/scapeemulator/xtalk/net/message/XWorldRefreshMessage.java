package net.scapeemulator.xtalk.net.message;

import java.io.IOException;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.worldlist.OfflineWorld;
import net.scapeemulator.worldlist.OnlineWorld;
import net.scapeemulator.worldlist.World;
import net.scapeemulator.xtalk.client.CrosstalkClient;

public class XWorldRefreshMessage implements Message {
	public static final int OPCODE = 1;
	private final World[] worlds;

	public XWorldRefreshMessage(World[] worlds) {
		this.worlds = worlds;
	}

	public World[] getWorlds() {
		return worlds;
	}

	public static class Decoder extends MessageDecoder<XWorldRefreshMessage> {

		public Decoder() {
			super(OPCODE);
		}

		@Override
		public XWorldRefreshMessage decode(Packet packet) throws IOException {
			try {
				PacketReader reader = new PacketReader(packet);
				int min = reader.getUnsignedSmart();
				int amt = reader.getUnsignedSmart();
				World[] worlds = new World[amt];
				for (int i = 0; i < amt; i++) {
					worlds[i] = decode(reader, min);
				}
				return new XWorldRefreshMessage(worlds);
			} catch (Exception e) {
				e.printStackTrace();
				throw new IOException();
			}
		}

		private World decode(PacketReader reader, int min) {
			int id = (int) (reader.getUnsigned(DataType.SHORT) + min);
			int country = (int) reader.getUnsigned(DataType.BYTE);
			int flags = (int) reader.getUnsigned(DataType.INT);
			String activity = reader.getString();
			String ipAdress = reader.getString();
			if (ipAdress.equalsIgnoreCase("world_offline")) {
				return new OfflineWorld(id, flags, country, activity);
			} else {
				return new OnlineWorld(id, flags, country, activity, ipAdress);
			}
		}
	}

	public static class Handler extends MessageHandler<CrosstalkClient, XWorldRefreshMessage> {

		@Override
		public void handle(CrosstalkClient target, XWorldRefreshMessage message) {
			target.getGameServer().getWorldListService().setWorlds(message.getWorlds());
		}
	}
}
