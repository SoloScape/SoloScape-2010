package net.scapeemulator.js5;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

public final class FileResponseEncoder extends MessageToByteEncoder<FileResponse> {

	@Override
	public void encode(ChannelHandlerContext ctx, FileResponse response, ByteBuf buf) throws IOException {
		ByteBuf container = response.getContainer();
		try {
			buf.writeByte(response.getType());
			buf.writeShort(response.getFile());

			int compression = container.readUnsignedByte();
			if (!response.isPriority())
				compression |= 0x80;
			buf.writeByte(compression);

			// Write data directly from the container. readBytes(int) would
			// allocate an additional ByteBuf for every JS5 chunk.
			int bytes = Math.min(container.readableBytes(), 508);
			buf.writeBytes(container, bytes);
			while (container.isReadable()) {
				buf.writeByte(0xFF);
				bytes = Math.min(container.readableBytes(), 511);
				buf.writeBytes(container, bytes);
			}
		} finally {
			container.release();
		}
	}
}
