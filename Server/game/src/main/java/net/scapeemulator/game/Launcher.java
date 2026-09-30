package net.scapeemulator.game;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import net.scapeemulator.game.content.tutorial.TutorialIsland;
import net.scapeemulator.util.NetworkConstants;

public class Launcher {
	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);
	public static final List<Plugin> plugins = new ArrayList<>();

	public static void registerPlugins() {
		plugins.add(new TutorialIsland());
		plugins.add(new DefaultGameLogic());
		plugins.add(new ContentPlugin());
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			logger.error("Usage: GameServer [worldId]");
			return;
		}
		registerPlugins();
		try {
			InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
			int worldId = Integer.parseInt(args[0]);
			GameServer server = new GameServer(worldId);
			try {
				server.httpBind(new InetSocketAddress(NetworkConstants.HTTP_PORT));
			} catch (Throwable t) {
				/* TODO: fix Netty's diagnostic message in this case */
				logger.warn("Failed to bind to HTTP port." + t.getMessage());
			}
			server.httpBind(new InetSocketAddress(NetworkConstants.HTTP_ALT_PORT));
			server.serviceBind(new InetSocketAddress(NetworkConstants.GAME_PORT));
			server.serviceBind(new InetSocketAddress(NetworkConstants.WORLD_PORT + worldId));

			server.start();
		} catch (Exception e) {
			logger.error("Failed to start server.", e);
		}
	}
}
