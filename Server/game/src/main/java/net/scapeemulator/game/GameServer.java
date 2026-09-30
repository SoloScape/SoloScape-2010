package net.scapeemulator.game;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.scapeemulator.api.Service;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.cache.VarBitDefinition;
import net.scapeemulator.game.command.CommandDispatcher;
import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.game.io.DummyPlayerSerializer;
import net.scapeemulator.game.io.JdbcPlayerSerializer;
import net.scapeemulator.game.io.PlayerSerializer;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.def.*;
import net.scapeemulator.game.model.map.WorldMap;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.Messages;
import net.scapeemulator.game.net.HttpChannelInitializer;
import net.scapeemulator.game.net.RsChannelInitializer;
import net.scapeemulator.game.net.login.LoginService;
import net.scapeemulator.game.util.LandscapeKeyTable;
import net.scapeemulator.js5.UpdateService;
import net.scapeemulator.worldlist.WorldListService;
import net.scapeemulator.xtalk.AbstractCrosstalkClient;
import net.scapeemulator.xtalk.client.CrosstalkClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketAddress;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class GameServer implements Service {
	private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final EventLoopGroup loopGroup = new NioEventLoopGroup();
	private final ServerBootstrap serviceBootstrap = new ServerBootstrap();
	private final ServerBootstrap httpBootstrap = new ServerBootstrap();
	private final MessageDispatcher<Player> messageDispatcher;
	private final CommandDispatcher commandDispatcher;// Other place?
	private final WorldConfiguration worldConfiguration;

	private final World world;
	private final UpdateService updateService;
	private final LoginService loginService;
	private final Cache cache;
	private final LandscapeKeyTable landscapeKeyTable;
	private final CodecRepository codecRepository;
	private final WorldListService worldlistService;
	private final int version = 592, worldId;
	private AbstractCrosstalkClient crosstalk;

	public GameServer(int worldId) throws Exception {
		this.worldId = worldId;
		logger.info("Starting ScapeEmulator game server...");
		worldConfiguration = WorldConfiguration.parse("data/world.conf");
		world = World.getWorld(worldConfiguration);

		commandDispatcher = new CommandDispatcher();
		logger.info("Loading cache, related data and starting services.");

		/* load landscape keys */
		landscapeKeyTable = LandscapeKeyTable.open("data/landscape-keys");

		/* load game cache */
		cache = new Cache(FileStore.open("../resources/cache"));
		updateService = new UpdateService(version, cache);
		VarBitDefinition.init(cache);
		ItemDefinition.init(cache, worldConfiguration.isMembers());
		EquipmentDefinition.init();
		SceneryDefinition.init(cache);
		GameConstants.init(cache);
		WorldMap.init(cache, landscapeKeyTable);
		logger.info("Type definitions loaded... Registering plugins...");

		/* load message codecs and dispatcher */
		codecRepository = new CodecRepository();
		messageDispatcher = new MessageDispatcher<Player>();
		Messages.init(this);

		for (Plugin plugin : Launcher.plugins) {
			plugin.register(this);
			logger.info("Plugin " + plugin.getClass().getSimpleName() + " registered.");
		}

		/* load player serializer from config file */
		PlayerSerializer serializer = createPlayerSerializer();
		logger.info("Using serializer: " + serializer + ".");

		/* load Crosstalk client from config file */
		worldlistService = new WorldListService();
		crosstalk = createCrosstalkConnection();
		logger.info("Using Crosstalk client: " + crosstalk.getClass().getName() + ".");
		loginService = new LoginService(serializer, version);

		/* start netty */
		httpBootstrap.group(loopGroup);
		httpBootstrap.channel(NioServerSocketChannel.class);
		httpBootstrap.childHandler(new HttpChannelInitializer());
		serviceBootstrap.group(loopGroup);
		serviceBootstrap.channel(NioServerSocketChannel.class);
		serviceBootstrap.childHandler(new RsChannelInitializer(this));
	}

	private AbstractCrosstalkClient createCrosstalkConnection() throws IOException {
		Properties properties = new Properties();
		try (InputStream is = new FileInputStream("data/crosstalk.conf")) {
			properties.load(is);
		}

		String type = (String) properties.get("type");
		switch (type) {
		case "dummy":
			return null;
		case "local":
		case "adress":
			String host = type.equalsIgnoreCase("adress") ? (String) properties.get("ip") : "127.0.0.1";
			return new CrosstalkClient(this, host);
		default:
			throw new IOException("unknown crosstalk type! " + type);
		}
	}

	private PlayerSerializer createPlayerSerializer() throws IOException, SQLException {
		Properties properties = new Properties();
		try (InputStream is = new FileInputStream("data/serializer.conf")) {
			properties.load(is);
		}

		String type = (String) properties.get("type");
		switch (type) {
		case "dummy":
			return new DummyPlayerSerializer();
		case "jdbc":
			String url = (String) properties.get("url");
			String username = (String) properties.get("username");
			String password = (String) properties.get("password");
			return new JdbcPlayerSerializer(url, username, password);

		default:
			throw new IOException("unknown serializer type");
		}
	}

	public void httpBind(SocketAddress address) throws InterruptedException {
		logger.info("Binding to HTTP address: " + address + "...");
		httpBootstrap.localAddress(address).bind().sync();
	}

	public void serviceBind(SocketAddress address) throws InterruptedException {
		logger.info("Binding to service address: " + address + "...");
		serviceBootstrap.localAddress(address).bind().sync();
	}

	public void start() {
		logger.info("Ready for connections.");

		/* start login and update services */
		executor.submit(loginService);
		executor.submit(crosstalk);
		executor.submit(updateService);

		/* main game tick loop */
		for (;;) {
			long start = System.currentTimeMillis();
			tick();
			long elapsed = (System.currentTimeMillis() - start);
			long waitFor = 600 - elapsed;
			if (waitFor >= 0) {
				try {
					Thread.sleep(waitFor);
				} catch (InterruptedException e) {
					/* ignore */
				}
			}
		}
	}

	private void tick() {
		/*
		 * As the MobList class is not thread-safe, players must be registered
		 * within the game logic processing code.
		 */
		loginService.registerNewPlayers(world);
		world.tick();
	}

	public World getWorld() {
		return world;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public UpdateService getUpdateService() {
		return updateService;
	}

	public Cache getCache() {
		return cache;
	}

	public CodecRepository getCodecRepository() {
		return codecRepository;
	}

	public MessageDispatcher<Player> getMessageDispatcher() {
		return messageDispatcher;
	}

	public LandscapeKeyTable getLandscapeKeyTable() {
		return landscapeKeyTable;
	}

	public WorldListService getWorldListService() {
		return worldlistService;
	}

	public int getWorldId() {
		return worldId;
	}

	public int getVersion() {
		return version;
	}

	public WorldConfiguration getWorldConfiguration() {
		return worldConfiguration;
	}

	public CommandDispatcher getCommandDispatcher() {
		return commandDispatcher;
	}
}
