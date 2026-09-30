package net.scapeemulator.xtalk.net;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.scapeemulator.xtalk.CrosstalkServer;
import net.scapeemulator.xtalk.net.handshake.HandshakeDecoder;

public final class CrosstalkChannelInitializer extends ChannelInitializer<SocketChannel> {

	private final CrosstalkServer server;

	public CrosstalkChannelInitializer(CrosstalkServer server) {
		this.server = server;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new HandshakeDecoder(), new CrosstalkChannelHandler(server));
		ch.read();
	}
}
