package net.scapeemulator.xtalk;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import net.scapeemulator.api.Service;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.js5.UpdateService;
import net.scapeemulator.util.NetworkConstants;
import net.scapeemulator.xtalk.net.CrosstalkChannelInitializer;
import net.scapeemulator.xtalk.server.GameServerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class CrosstalkServer implements Service {
	private static final Logger logger = LoggerFactory.getLogger(CrosstalkServer.class);
	private static final boolean LOCAL = true;
	private static final int VERSION = 592;
	
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final ServerBootstrap bootstrap = new ServerBootstrap();
	private final GameServerService serverService;
	private final UpdateService js5;
	private final Cache cache;

	public static void main(String[] args) {
		try {
			InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());

			CrosstalkServer server = new CrosstalkServer();
			server.bind(new InetSocketAddress(NetworkConstants.WORLD_PORT));
			if (!LOCAL) {
				try {
					server.bind(new InetSocketAddress(NetworkConstants.GAME_PORT));
				} catch (Throwable t) {
					logger.info("Port " + NetworkConstants.GAME_PORT + " was left unbound. World Server online?");
				}
			}
			server.start();
		} catch (Throwable t) {
			logger.error("Failed to start server.", t);
		}
	}

	public CrosstalkServer() throws IOException {
		cache = new Cache(FileStore.open("../resources/cache"));
		js5 = new UpdateService(VERSION, cache);
		serverService = new GameServerService(VERSION);
		logger.info("Starting ScapeEmulator login server...");
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.childHandler(new CrosstalkChannelInitializer(this));
	}

	public void bind(SocketAddress address) throws InterruptedException {
		logger.info("Binding to address: " + address + "...");
		bootstrap.localAddress(address).bind().sync();
	}

	public void start() {
		executor.submit(js5);
		executor.submit(serverService);
		logger.info("Ready for connections.");
		for (;;) {
			long time = System.currentTimeMillis();
			// Processing logic here

			serverService.ping();
			time = System.currentTimeMillis() - time;
			if (time < 100l) {
				try {
					Thread.sleep(100l - time);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public UpdateService getJs5() {
		return js5;
	}

	public GameServerService getServerService() {
		return serverService;
	}
}
