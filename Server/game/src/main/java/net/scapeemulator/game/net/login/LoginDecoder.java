package net.scapeemulator.game.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.scapeemulator.api.ByteBufUtils;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.util.Base37Utils;
import net.scapeemulator.util.crypto.RsaKeySet;

import java.io.IOException;

public final class LoginDecoder extends ByteToMessageDecoder {

	private enum State {
		READ_HEADER, READ_PAYLOAD
	}

	private int hash, type, size;
	private State state = State.READ_HEADER;

	@Override
	public void decode(ChannelHandlerContext ctx, ByteBuf buf, MessageBuf<Object> out) throws IOException {
		if (state == State.READ_HEADER) {
			if (buf.readableBytes() < 4) {
				return;
			}

			state = State.READ_PAYLOAD;
			hash = buf.readUnsignedByte();
			type = buf.readUnsignedByte();
			size = buf.readUnsignedShort();

			if (type != 16 && type != 18)
				throw new IOException("Invalid login type.");
		}

		if (state == State.READ_PAYLOAD) {
			if (buf.readableBytes() < size) {
				return;
			}

			int version = buf.readInt();
			buf.readUnsignedByte();// Unknown

			// 1 fixed, 2 & 3 resized
			int displayMode = buf.readUnsignedByte();
			DisplayMode displ = DisplayMode.FIXED;
			if (displayMode >= 0 && displayMode < DisplayMode.values().length) {
				displ = DisplayMode.values()[displayMode];
			}

			buf.readUnsignedShort();// Width
			buf.readUnsignedShort();// height

			buf.readUnsignedByte();// Toolkit ID

			byte[] uid = new byte[24];
			for (int i = 0; i < uid.length; i++)
				uid[i] = buf.readByte();

			ByteBufUtils.readString(buf);

			// int affiliate =
			buf.readInt();
			int toolkitSettingSize = buf.readUnsignedByte();
			buf.readBytes(toolkitSettingSize);

			// Cached id for player packets
			int seqNum = buf.readShort();

			int[] crc = new int[30];
			for (int i = 0; i < crc.length; i++)
				crc[i] = buf.readInt();

			int encryptedSize = buf.readUnsignedByte();
			ByteBuf secureBuffer = ByteBufUtils.rsa(buf.readBytes(encryptedSize), RsaKeySet.MODULUS,
					RsaKeySet.PRIVATE_KEY);

			int encryptedType = secureBuffer.readUnsignedByte();
			if (encryptedType != 10)
				throw new IOException("Invalid encrypted block type.");

			long csk = secureBuffer.readLong();
			long ssk = secureBuffer.readLong();

			long encodedUsername = secureBuffer.readLong();
			String username = Base37Utils.decodeBase37(encodedUsername);
			String password = ByteBufUtils.readString(secureBuffer);

			if (((encodedUsername >> 16) & 31) != hash)
				throw new IOException("Username hash mismatch.");

			boolean reconnecting = type == 16;
			out.add(new LoginRequest(reconnecting, username, password, csk, ssk, version, seqNum, crc, displ));

			ctx.pipeline().remove(this);
		}
	}

}
