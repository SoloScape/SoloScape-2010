package net.scapeemulator.game.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class LoginEncoder extends MessageToByteEncoder<LoginResponse> {

	@Override
	public void encode(ChannelHandlerContext ctx, LoginResponse response, ByteBuf buf) {
		ByteBuf payload = response.getPayload();
		try {
			buf.writeByte(response.getStatus());
			buf.writeBytes(payload);
		} finally {
			// Key exchange and successful logins use pooled temporary buffers.
			payload.release();
		}

		if (response.getStatus() != LoginResponse.STATUS_EXCHANGE_KEYS)
			ctx.pipeline().remove(this);
	}
}
