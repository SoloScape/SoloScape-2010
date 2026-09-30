package net.scapeemulator.xtalk.server.message.world;

import java.io.IOException;
import java.util.Map;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.Packet.Type;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.worldlist.Country;

public class XCountryTypeRefreshMessage implements Message {
	private final Map<Integer, Country> countryList;
	private static final int OPCODE = 2;

	public XCountryTypeRefreshMessage(Map<Integer, Country> countryList) {
		this.countryList = countryList;
	}

	public static class Encoder extends MessageEncoder<XCountryTypeRefreshMessage> {

		public Encoder() {
			super(XCountryTypeRefreshMessage.class);
		}

		@Override
		public Packet encode(ByteBufAllocator alloc, XCountryTypeRefreshMessage message) throws IOException {
			PacketBuilder builder = new PacketBuilder(alloc, OPCODE, Type.VARIABLE_SHORT);
			builder.put(DataType.BYTE, message.countryList.size());
			for(Country country : message.countryList.values()) {
				builder.put(DataType.BYTE, country.getFlag());
				builder.putString(country.getName());
			}
			return builder.toPacket();
		}
	}
}
