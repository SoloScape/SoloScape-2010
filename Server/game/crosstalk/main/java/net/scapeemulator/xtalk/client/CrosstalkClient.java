package net.scapeemulator.xtalk.client;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.game.GameServer;
import net.scapeemulator.xtalk.*;
import net.scapeemulator.xtalk.client.login.CrosstalkLoginSession;
import net.scapeemulator.xtalk.net.XTalkMessages;

public class CrosstalkClient extends AbstractCrosstalkClient {
	private static final Logger logger = LoggerFactory.getLogger(CrosstalkClient.class);
	private final EventLoopGroup loopGroup = new NioEventLoopGroup();
	private AbstractCrosstalkSession<CrosstalkClient> session;

	private MessageDispatcher<CrosstalkClient> dispatcher = new MessageDispatcher<>();
	private CodecRepository repo = new CodecRepository();

	private final String host;
	private int port = 40000;
	private final GameServer gameServer;

	public CrosstalkClient(GameServer gameServer, String host) {
		super(gameServer.getWorldId(), gameServer.getVersion(), gameServer.getUpdateService());
		this.gameServer = gameServer;
		this.host = host;
		try {
			XTalkMessages.init(repo, dispatcher);
			initSession();
		} catch (Exception e) {
			port = port == 40000 ? 443 : 40000;
			logger.info("Failed to contact crosstalk server. Retrying to connect every 1 second. " + e.getMessage());
		}
	}

	private void initSession() throws IOException, InterruptedException {
		latestContactAttempt = System.currentTimeMillis();

		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(loopGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.handler(new CrosstalkChannelInitializer(this));

		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
		Channel channel = future.channel();
		session = new CrosstalkLoginSession(this, gameServer, channel).requestLogin();
		logger.info("Succesfully connected to crosstalk server!");
	}

	public void channelClosed() {
		session = null;
	}

	@Override
	public void run() {
		for (;;) {
			if (session != null) {
				if (session instanceof CrosstalkSession) {
					((CrosstalkSession) session).processMessages();
				}
			} else {
				long time = System.currentTimeMillis();
				if (time - latestContactAttempt > 2000L) {
					try {
						initSession();
					} catch (Exception e) {
						port = port == 40000 ? 443 : 40000;
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "CrosstalkClient [" + host + ":" + port + "]";
	}

	public AbstractCrosstalkSession<CrosstalkClient> getSession() {
		return session;
	}

	public void setSession(AbstractCrosstalkSession<CrosstalkClient> session) {
		this.session = session;
	}

	@Override
	public CodecRepository getRepo() {
		return repo;
	}

	public MessageDispatcher<CrosstalkClient> getDispatcher() {
		return dispatcher;
	}

	public GameServer getGameServer() {
		return gameServer;
	}

	private long latestContactAttempt;
}
