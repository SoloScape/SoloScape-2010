package net.scapeemulator.game.msg.interf.util;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.api.net.packet.Packet.Type;

import java.io.IOException;

public final class ScriptMessageEncoder extends MessageEncoder<ScriptMessage> {

	public ScriptMessageEncoder() {
		super(ScriptMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, ScriptMessage message) throws IOException {
		int id = message.getId();
		String types = message.getTypes();
		Object[] parameters = message.getParameters();

		PacketBuilder builder = new PacketBuilder(alloc, 27, Type.VARIABLE_SHORT);
		builder.put(DataType.SHORT, message.getSequenceNumber());
		builder.putString(types);

		for (int i = types.length() - 1; i >= 0; i--) {
			if (types.charAt(i) == 's') {
				builder.putString((String) parameters[i]);
			} else {
				builder.put(DataType.INT, ((Number) parameters[i]).intValue());
			}
		}

		builder.put(DataType.INT, id);
		return builder.toPacket();
	}

}
