package net.scapeemulator.api.message.codec.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.Packet.Type;

import java.io.IOException;

public final class PacketDecoder extends ByteToMessageDecoder {
	private final int[] sizes;

	private enum State {
		READ_OPCODE, READ_SIZE, READ_PAYLOAD
	}

	private final StreamCipher cipher;
	private State state = State.READ_OPCODE;
	private boolean variable;
	private int opcode, size;

	public PacketDecoder(StreamCipher cipher, int[] sizes) {
		this.cipher = cipher;
		this.sizes = sizes;
	}

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf buf, MessageBuf<Object> out) throws Exception {
		if (state == State.READ_OPCODE) {
			if (!buf.isReadable())
				return;

			opcode = (buf.readUnsignedByte() - cipher.nextInt()) & 0xFF;
			size = sizes[opcode];

			if (size == -3)
				throw new IOException("Illegal opcode " + opcode + ".");

			variable = size < 0;
			state = variable ? State.READ_SIZE : State.READ_PAYLOAD;
		}

		if (state == State.READ_SIZE) {
			if (!buf.isReadable())
				return;
			switch (size) {
			case -1:
				size = buf.readUnsignedByte();
				break;
			case -2:
				size = buf.readUnsignedShort();
				break;

			default:
				break;
			}
			state = State.READ_PAYLOAD;
		}

		if (state == State.READ_PAYLOAD) {
			if (buf.readableBytes() < size)
				return;

			ByteBuf payload = buf.readBytes(size);
			state = State.READ_OPCODE;
			out.add(new Packet(opcode, variable ? Type.VARIABLE_BYTE : Type.FIXED, payload));
		}
	}
}
