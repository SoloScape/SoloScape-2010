package net.scapeemulator.xtalk.net.login;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.scapeemulator.api.ByteBufUtils;
import net.scapeemulator.worldlist.Country;

public class WorldLoginDecoder extends ByteToMessageDecoder {
	private WorldLoginState state = WorldLoginState.READ_HEADER;
	private int size;

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, MessageBuf<Object> out) throws Exception {
		switch (state) {
		case READ_HEADER:
			if (buf.readableBytes() < 2)
				return;
			state = WorldLoginState.READ_PAYLOAD;
			size = buf.readUnsignedShort();
			break;
		case READ_PAYLOAD:
			if (buf.readableBytes() < size)
				return;
			int version = buf.readInt();
			int id = buf.readUnsignedByte();

			int flags = buf.readUnsignedByte();
			String activity = ByteBufUtils.readString(buf);

			int ixCount = buf.readUnsignedByte();
			int[] crc = new int[ixCount];
			for (int i = 0; i < crc.length; i++)
				crc[i] = buf.readInt();
			int flag = buf.readUnsignedByte();
			String countryName = ByteBufUtils.readString(buf);
			Country country = new Country(flag, countryName);
			int encryptedSize = buf.readUnsignedByte();
			// TODO: Encryption? Country code?
			ByteBuf secureBuffer = buf.readBytes(encryptedSize);

			int encryptedType = secureBuffer.readUnsignedByte();
			if (encryptedType != 10)
				throw new IOException("Invalid encrypted block type.");

			long clientSessionKey = secureBuffer.readLong();
			long serverSessionKey = secureBuffer.readLong();
			out.add(new WorldLoginRequest(version, id, flags, activity,country, crc, clientSessionKey, serverSessionKey));
			ctx.pipeline().remove(this);
			break;
		}
	}
}
