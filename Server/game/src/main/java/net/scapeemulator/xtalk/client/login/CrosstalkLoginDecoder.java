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
				ServerLoginResult result = parseLoginResponse(buf);
				if (result == null)
					return;
				out.add(result);
				ctx.pipeline().remove(this);
				break;
			}
		}
	}

	/**
	 * Failure packets contain only the status byte. Success packets contain
	 * a country count and a variable-length list of terminated country names.
	 * Inspect the complete success packet before consuming its first byte:
	 * TCP is allowed to deliver a packet in arbitrary fragments.
	 */
	static ServerLoginResult parseLoginResponse(ByteBuf buf) {
		int start = buf.readerIndex();
		int end = buf.writerIndex();
		if (start >= end)
			return null;
		int status = buf.getUnsignedByte(start);
		if (status != ServerLoginResult.SUCCESS) {
			buf.readUnsignedByte();
			return new ServerLoginResult(status, new HashMap<Integer, Country>());
		}
		if (end - start < 2)
			return null;

		int countryCount = buf.getUnsignedByte(start + 1);
		int cursor = start + 2;
		for (int i = 0; i < countryCount; i++) {
			if (cursor >= end)
				return null; // Country flag not yet received.
			cursor++;
			while (cursor < end && buf.getUnsignedByte(cursor) != 0)
				cursor++;
			if (cursor >= end)
				return null; // Country name is incomplete.
			cursor++;
		}

		buf.skipBytes(2);
		Map<Integer, Country> countries = new HashMap<>();
		for (int i = 0; i < countryCount; i++)
			countries.put(i, new Country(buf.readUnsignedByte(), ByteBufUtils.readString(buf)));
		return new ServerLoginResult(status, countries);
	}
}
