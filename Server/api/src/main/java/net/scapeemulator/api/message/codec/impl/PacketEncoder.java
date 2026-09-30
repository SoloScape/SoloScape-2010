package net.scapeemulator.api.message.codec.impl;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.Packet.Type;

public final class PacketEncoder extends MessageToByteEncoder<Packet> {

	private final StreamCipher cipher;

	public PacketEncoder(StreamCipher cipher) {
		this.cipher = cipher;
	}

	@Override
	public void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf buf) throws Exception {
		Type type = packet.getType();
		ByteBuf payload = packet.getPayload();

		buf.writeByte(packet.getOpcode() + cipher.nextInt());
		if (type == Type.VARIABLE_BYTE)
			buf.writeByte(payload.readableBytes());
		else if (type == Type.VARIABLE_SHORT)
			buf.writeShort(payload.readableBytes());

		buf.writeBytes(payload);
	}
}
