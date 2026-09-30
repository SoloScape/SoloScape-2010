package net.scapeemulator.xtalk.net.handshake;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundByteHandlerAdapter;
import io.netty.channel.ChannelPipeline;

import net.scapeemulator.js5.FileResponseEncoder;
import net.scapeemulator.js5.UpdateDecoder;
import net.scapeemulator.js5.UpdateStatusMessageEncoder;
import net.scapeemulator.js5.XorEncoder;
import net.scapeemulator.worldlist.WorldListDecoder;
import net.scapeemulator.worldlist.WorldListEncoder;
import net.scapeemulator.xtalk.net.autoworld.AutoWorldDecoder;
import net.scapeemulator.xtalk.net.autoworld.AutoWorldEncoder;
import net.scapeemulator.xtalk.net.login.WorldLoginDecoder;
import net.scapeemulator.xtalk.net.login.WorldLoginEncoder;

public class HandshakeDecoder extends ChannelInboundByteHandlerAdapter {

	@Override
	protected void inboundBufferUpdated(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		if (!buf.isReadable())
			return;

		int service = buf.readUnsignedByte();
		ByteBuf additionalBuf = null;
		if (buf.isReadable()) {
			additionalBuf = buf.readBytes(buf.readableBytes());
		}

		ChannelPipeline pipeline = ctx.pipeline();
		pipeline.remove(HandshakeDecoder.class);

		switch (service) {
		case HandshakeMessage.SERVICE_UPDATE:
			pipeline.addFirst(new FileResponseEncoder(), new UpdateStatusMessageEncoder(), new XorEncoder(),
					new UpdateDecoder());
			break;
		case HandshakeMessage.SERVICE_WORLD_LIST:
			pipeline.addFirst(new WorldListEncoder(), new WorldListDecoder());
			break;
		case HandshakeMessage.SERVICE_WORLD_LOGIN:
			pipeline.addFirst(new WorldLoginEncoder(), new WorldLoginDecoder());
			break;
		case HandshakeMessage.SERVICE_AUTO_LOGIN:
			pipeline.addFirst(new AutoWorldEncoder(), new AutoWorldDecoder());
			break;
		case HandshakeMessage.SERVICE_LOGIN:
			ctx.channel().close();
			break;
		default:
			throw new IOException("Invalid service id: " + service + ".");
		}

		ctx.nextInboundMessageBuffer().add(new HandshakeMessage(service));
		ctx.fireInboundBufferUpdated();

		if (additionalBuf != null) {
			ChannelHandlerContext head = ctx.pipeline().firstContext();
			head.nextInboundByteBuffer().writeBytes(additionalBuf);
			head.fireInboundBufferUpdated();
		}
	}
}
