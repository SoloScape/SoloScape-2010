package net.scapeemulator.xtalk.net.message;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.worldlist.Country;
import net.scapeemulator.xtalk.client.CrosstalkClient;

public class XCountryTypeRefreshMessage implements Message {
	public static final int OPCODE = 2;
	private Map<Integer, Country> countries;

	public Map<Integer, Country> getCountries() {
		return countries;
	}

	public static class Decoder extends MessageDecoder<XCountryTypeRefreshMessage> {

		public Decoder() {
			super(OPCODE);
		}

		@Override
		public XCountryTypeRefreshMessage decode(Packet packet) throws IOException {
			PacketReader reader = new PacketReader(packet);
			int count = (int) reader.getUnsigned(DataType.BYTE);
			Map<Integer, Country> countries = new HashMap<>();
			for (int id = 0; id < count; id++) {
				countries.put(id, new Country((int) reader.getUnsigned(DataType.BYTE), reader.getString()));
			}
			XCountryTypeRefreshMessage message = new XCountryTypeRefreshMessage();
			message.countries = countries;
			return message;
		}
	}

	public static class Handler extends MessageHandler<CrosstalkClient, XCountryTypeRefreshMessage> {

		@Override
		public void handle(CrosstalkClient target, XCountryTypeRefreshMessage message) {
			target.getGameServer().getWorldListService().setCountries(message.getCountries());
		}
	}
}
