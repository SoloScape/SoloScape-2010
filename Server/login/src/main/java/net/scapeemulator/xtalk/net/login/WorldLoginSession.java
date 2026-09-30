package net.scapeemulator.xtalk.net.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPipeline;
import net.scapeemulator.api.ByteBufUtils;
//import net.burtleburtle.bob.rand.IsaacRandom;
import net.scapeemulator.api.Session;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.message.codec.impl.*;
import net.scapeemulator.cache.ChecksumTable;
import net.scapeemulator.util.crypto.ZeroStreamCipher;
import net.scapeemulator.worldlist.Country;
import net.scapeemulator.xtalk.CrosstalkServer;
import net.scapeemulator.xtalk.net.CrosstalkChannelHandler;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;
import net.scapeemulator.xtalk.server.GameServerService;
import net.scapeemulator.xtalk.server.message.Messages;

public class WorldLoginSession extends Session<CrosstalkServer> {
	private static final SecureRandom random = new SecureRandom();

	private final long serverSessionKey = random.nextLong();
	private StreamCipher inRandom, outRandom;

	public WorldLoginSession(CrosstalkServer service, Channel channel) {
		super(service, channel);
		init();
	}

	private void init() {
		ByteBuf buf = channel.alloc().buffer(8);
		buf.writeLong(serverSessionKey);
		channel.write(new WorldLoginMessage(WorldLoginMessage.STATUS_EXCHANGE_KEYS, buf));
		channel.flush();
	}

	public void sendSuccess(int status, GameServerSession session) {
		GameServerService service = this.service.getServerService();

		ByteBuf buf = session.getChannel().alloc().buffer();
		Map<Integer, Country> countries = service.getCountryList();
		buf.writeByte(countries.size());
		Iterator<Entry<Integer, Country>> iter = countries.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Integer, Country> next = iter.next();
			buf.writeByte(next.getValue().getFlag());
			ByteBufUtils.writeString(buf, next.getValue().getName());
		}

		WorldLoginMessage response = new WorldLoginMessage(status, buf);
		channel.write(response);
		channel.flush();

		ChannelPipeline pipeline = channel.pipeline();

		pipeline.addFirst(new PacketEncoder(outRandom), new OutgoingMessageEncoder(service.getCodecRepository()),
				new PacketDecoder(inRandom, Messages.SIZES), new IncomingMessageDecoder(service.getCodecRepository()));
		CrosstalkChannelHandler handler = pipeline.get(CrosstalkChannelHandler.class);
		handler.setSession(session);

	}

	public void sendFailure(int status) {
		WorldLoginMessage response = new WorldLoginMessage(status);
		channel.write(response).addListener(ChannelFutureListener.CLOSE);
		channel.flush();
	}

	@Override
	public void messageReceived(Object message) throws IOException {
		WorldLoginRequest request = (WorldLoginRequest) message;
		boolean versionMismatch = false;
		if (request.getVersion() != service.getJs5().getVersion()) {
			versionMismatch = true;
		}
		if (request.getServerSessionKey() != serverSessionKey) {
			sendFailure(WorldLoginMessage.KEY_MISMATCH);
			return;
		}
		ChecksumTable table = service.getJs5().getChecksumTable();
		int[] crc = request.getCrc();
		for (int i = 0; i < crc.length; i++) {
			if (table.getEntry(i).getCrc() != crc[i]) {
				versionMismatch = true;
				break;
			}
		}
		if (versionMismatch) {
			sendFailure(WorldLoginMessage.INVALID_VERSION);
			return;
		}

		long clientSessionKey = request.getClientSessionKey();
		long serverSessionKey = request.getServerSessionKey();

		int[] seed = new int[4];

		seed[0] = (int) (clientSessionKey >> 32);
		seed[1] = (int) clientSessionKey;
		seed[2] = (int) (serverSessionKey >> 32);
		seed[3] = (int) serverSessionKey;

		inRandom = new ZeroStreamCipher();// new IsaacRandom(seed);
		for (int i = 0; i < seed.length; i++)
			seed[i] += 50;
		outRandom = new ZeroStreamCipher();// new IsaacRandom(seed);

		service.getServerService().addLoginRequest(this, request);
	}

	public String getAdress() {
		InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
		InetAddress inetaddress = socketAddress.getAddress();
		return inetaddress.getHostAddress();
	}

	public Channel getChannel() {
		return channel;
	}
}
