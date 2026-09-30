package net.scapeemulator.game.msg.util;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.api.net.packet.Packet.Type;

public final class ServerMessageEncoder extends MessageEncoder<ServerMessage> {

	public ServerMessageEncoder() {
		super(ServerMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, ServerMessage message) {
		PacketBuilder builder = new PacketBuilder(alloc, 26, Type.VARIABLE_BYTE);
		builder.putSmart(message.getIndex());
		builder.put(DataType.INT, 0);
		builder.put(DataType.BYTE, 0);// Flags, TODO
		builder.putString(message.getText());
		return builder.toPacket();
	}

}
