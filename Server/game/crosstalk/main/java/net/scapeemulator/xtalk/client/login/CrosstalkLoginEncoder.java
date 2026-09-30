package net.scapeemulator.xtalk.client.login;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.scapeemulator.api.ByteBufUtils;
import net.scapeemulator.js5.UpdateService;
import net.scapeemulator.xtalk.login.XTalkLoginHandshake;
import net.scapeemulator.xtalk.login.XTalkLoginRequest;
import net.scapeemulator.xtalk.login.XTalkMessage;

public class CrosstalkLoginEncoder extends MessageToByteEncoder<XTalkMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, XTalkMessage message, ByteBuf buf) throws Exception {
		if (message instanceof XTalkLoginHandshake) {
			buf.writeByte(((XTalkLoginHandshake) message).getOpcode());
		} else if (message instanceof XTalkLoginRequest) {
			XTalkLoginRequest request = (XTalkLoginRequest) message;
			ByteBuf payload = ctx.channel().alloc().buffer();
			payload.writeInt(request.getVersion());
			payload.writeByte(request.getWorldId());
			payload.writeByte(request.getFlags());
			ByteBufUtils.writeString(payload, request.getActivity());

			UpdateService js5 = request.getJs5();
			int size = js5.getChecksumTable().getSize();
			payload.writeByte(size);
			for (int i = 0; i < size; i++) {
				payload.writeInt(js5.getChecksumTable().getEntry(i).getCrc());
			}
			payload.writeByte(request.getCountry().getFlag());
			ByteBufUtils.writeString(payload, request.getCountry().getName());

			ByteBuf secureBuf = ctx.alloc().buffer();
			secureBuf.writeByte(10);
			secureBuf.writeLong(request.getClientSessionKey());
			secureBuf.writeLong(request.getServerSessionKey());

			payload.writeByte(secureBuf.readableBytes());
			payload.writeBytes(secureBuf);
			ByteBuf packet = ctx.alloc().buffer();
			packet.writeShort(payload.readableBytes());
			packet.writeBytes(payload);
			buf.writeBytes(packet);
			ctx.pipeline().remove(this);
		}
	}
}
