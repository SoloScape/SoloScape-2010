package net.scapeemulator.xtalk.net.autoworld;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public final class AutoWorldEncoder extends MessageToByteEncoder<AutoWorldResponse> {

	@Override
	public void encode(ChannelHandlerContext ctx, AutoWorldResponse response, ByteBuf buf) {
		buf.writeByte(response.getStatus());
		buf.writeBytes(response.getPayload());
		ctx.pipeline().remove(this);
	}
}
