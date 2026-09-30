package net.scapeemulator.xtalk.net.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class WorldLoginEncoder extends MessageToByteEncoder<WorldLoginMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, WorldLoginMessage response, ByteBuf buf) throws Exception {
		buf.writeByte(response.getStatus());
		buf.writeBytes(response.getPayload());

		if (response.getStatus() != WorldLoginMessage.STATUS_EXCHANGE_KEYS)
			ctx.pipeline().remove(this);
	}
}
