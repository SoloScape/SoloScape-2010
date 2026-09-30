package net.scapeemulator.xtalk.client.login;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.scapeemulator.api.ByteBufUtils;
import net.scapeemulator.worldlist.Country;
import net.scapeemulator.xtalk.ServerLoginResult;
import net.scapeemulator.xtalk.client.CrosstalkClient;

public class CrosstalkLoginDecoder extends ByteToMessageDecoder {
	private CrosstalkClient client;

	public CrosstalkLoginDecoder(CrosstalkClient client) {
		this.client = client;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, MessageBuf<Object> out) throws Exception {
		if (client.getSession() instanceof CrosstalkLoginSession) {
			CrosstalkLoginSession session = (CrosstalkLoginSession) client.getSession();
			switch (session.getState()) {
			case EXCHANGE_KEYS:
				if (buf.readableBytes() < 9)
					return;
				int res = buf.readUnsignedByte();
				long serverSessionKey = buf.readLong();
				out.add(new ServerSessionKeyResult(res, serverSessionKey));
				break;
			case LOGIN:
				if (buf.readableBytes() < 1)
					return;
				res = buf.readUnsignedByte();
				Map<Integer, Country> countries = new HashMap<>();
				int cc = buf.readUnsignedByte();
				for (int i = 0; i < cc; i++) {
					countries.put(i, new Country(buf.readUnsignedByte(), ByteBufUtils.readString(buf)));
				}
				out.add(new ServerLoginResult(res, countries));
				ctx.pipeline().remove(this);
				break;
			}
		}
	}
}
