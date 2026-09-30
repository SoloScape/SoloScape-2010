package net.scapeemulator.xtalk.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import net.scapeemulator.xtalk.client.login.CrosstalkLoginDecoder;
import net.scapeemulator.xtalk.client.login.CrosstalkLoginEncoder;

public final class CrosstalkChannelInitializer extends ChannelInitializer<SocketChannel> {
	private final CrosstalkClient client;

	public CrosstalkChannelInitializer(CrosstalkClient client) {
		this.client = client;
	}

	@Override
	public void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new CrosstalkLoginEncoder(), new CrosstalkLoginDecoder(client),
				new CrosstalkChannelHandler(client));
	}

}
