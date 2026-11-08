package net.scapeemulator.game.io;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.mob.SkillSet;
import net.scapeemulator.game.model.player.*;
import net.scapeemulator.game.net.login.LoginResponse;
import net.scapeemulator.util.Base37Utils;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.Properties;

/** Local character profiles. Each completed save atomically replaces one file. */
public final class FilePlayerSerializer extends PlayerSerializer {
	private static final Logger logger = LoggerFactory.getLogger(FilePlayerSerializer.class);
	private static final int VERSION = 1;
	private static final int[] INVENTORIES = { Inventory.BACKPACK, Inventory.EQUIPMENT,
			Inventory.BANK, Inventory.GENERAL_STORE_FREEBIES };
	private final Path directory;

	public FilePlayerSerializer(Path directory) throws IOException {
		this.directory = directory.toAbsolutePath().normalize();
		Files.createDirectories(this.directory);
	}

	@Override
	public synchronized SerializeResult load(String username, String password) {
		try {
			String name = accountName(username);
			if (password == null || password.length() > 72)
				return new SerializeResult(LoginResponse.STATUS_INVALID_PASSWORD);
			Path file = profile(name);
			Player player;
			if (Files.exists(file)) {
				Properties data = read(file, name);
				if (!BCrypt.checkpw(password, required(data, "passwordHash")))
					return new SerializeResult(LoginResponse.STATUS_INVALID_PASSWORD);
				player = decode(data, name);
				player.setPassword(password);
			} else {
				// Preserve this server's starter character, rights and sample stock, once only.
				player = new DummyPlayerSerializer().load(name, password).getPlayer();
				write(file, encode(player, BCrypt.hashpw(password, BCrypt.gensalt())));
			}
			return new SerializeResult(LoginResponse.STATUS_OK, player);
		} catch (IOException | RuntimeException ex) {
			logger.error("Could not load character {}. Existing profile has been left intact.", username, ex);
			return new SerializeResult(LoginResponse.STATUS_ERROR_LOADING_PROFILE);
		}
	}

	@Override
	public synchronized void save(Player player) {
		try {
			String name = accountName(player.getUsername());
			Path file = profile(name);
			String hash = Files.exists(file) ? required(read(file, name), "passwordHash")
					: BCrypt.hashpw(player.getPassword(), BCrypt.gensalt());
			write(file, encode(player, hash));
			logger.info("Saved character {}.", name);
		} catch (IOException | RuntimeException ex) {
			throw new IllegalStateException("Could not save character " + player.getUsername(), ex);
		}
	}

	private static String accountName(String username) {
		if (username == null || !username.matches("[A-Za-z0-9_ ]{1,12}"))
			throw new IllegalArgumentException("Invalid character name");
		String name = Base37Utils.decodeBase37(Base37Utils.encodeBase37(username));
		if (name.isEmpty())
			throw new IllegalArgumentException("Empty character name");
		return name;
	}

	private Path profile(String name) {
		// Prefix also makes names such as CON safe on Windows.
		return directory.resolve("player-" + name + ".properties");
	}

	private Properties read(Path file, String name) throws IOException {
		if (Files.size(file) > 1024 * 1024)
			throw new IOException("Character profile is too large");
		Properties data = new Properties();
		try (InputStream input = Files.newInputStream(file)) {
			data.load(input);
		}
		if (integer(data, "version", 1, VERSION) != VERSION || !name.equals(required(data, "username")))
			throw new IOException("Character profile identity/version mismatch");
		required(data, "passwordHash");
		return data;
	}

	private void write(Path file, Properties data) throws IOException {
		Path temporary = Files.createTempFile(directory, ".character-", ".tmp");
		try {
			try (FileOutputStream output = new FileOutputStream(temporary.toFile())) {
				data.store(output, "SoloScape character profile - version " + VERSION);
				output.getChannel().force(true);
			}
			try {
				Files.move(temporary, file, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
			} catch (AtomicMoveNotSupportedException ex) {
				Files.move(temporary, file, StandardCopyOption.REPLACE_EXISTING);
			}
		} finally {
			Files.deleteIfExists(temporary);
		}
	}

	private Properties encode(Player player, String passwordHash) {
		Properties data = new Properties();
		put(data, "version", VERSION);
		put(data, "username", accountName(player.getUsername()));
		put(data, "passwordHash", passwordHash);
		put(data, "rights", player.getRights());
		Position position = player.getPosition();
		put(data, "position.x", position.getX());
		put(data, "position.y", position.getY());
		put(data, "position.height", position.getHeight());
		put(data, "energy", player.getPreciseEnergy());
		Appearance appearance = player.getAppearance();
		put(data, "appearance.confirmed", !player.isCharacterCreationRequired());
		put(data, "tutorial.controls.pending", player.isControlsTutorialPending());
		put(data, "appearance.gender", appearance.getGender().name());
		for (int i = 0; i < 7; i++) put(data, "appearance.style." + i, appearance.getStyle(i));
		for (int i = 0; i < 5; i++) put(data, "appearance.color." + i, appearance.getColor(i));
		SkillSet skills = player.getSkillSet();
		put(data, "skills.count", skills.size());
		for (int i = 0; i < skills.size(); i++) {
			put(data, "skill." + i + ".level", skills.getCurrentLevel(i));
			put(data, "skill." + i + ".maximum", skills.getMaximumLevel(i));
			put(data, "skill." + i + ".xp", skills.getExperience(i));
		}
		for (int id : INVENTORIES) {
			Inventory inventory = player.getInventories().get(id);
			if (inventory == null) {
				if (id != Inventory.GENERAL_STORE_FREEBIES)
					throw new IllegalArgumentException("Missing inventory " + id);
				continue;
			}
			Item[] items = inventory.toArray();
			put(data, "inventory." + id + ".size", items.length);
			for (int slot = 0; slot < items.length; slot++) {
				Item item = items[slot];
				if (item != null) put(data, "inventory." + id + ".slot." + slot, item.getId() + ":" + item.getAmount());
			}
		}
		for (Map.Entry<Integer, Integer> entry : player.getStatus().getPersistentVariables().entrySet())
			put(data, "varp." + entry.getKey(), entry.getValue());
		return data;
	}

	private Player decode(Properties data, String name) throws IOException {
		Player player = new Player();
		player.setUsername(name);
		player.setRights(integer(data, "rights", 0, 255));
		player.setPosition(new Position(integer(data, "position.x", 0, 16383),
				integer(data, "position.y", 0, 16383), integer(data, "position.height", 0, 3)));
		player.setEnergy(decimal(data, "energy", 0, 100));
		int[] styles = new int[7], colors = new int[5];
		for (int i = 0; i < styles.length; i++) styles[i] = integer(data, "appearance.style." + i, 0, 65535);
		for (int i = 0; i < colors.length; i++) colors[i] = integer(data, "appearance.color." + i, 0, 255);
		player.setAppearance(new Appearance(Gender.valueOf(required(data, "appearance.gender")), styles, colors));
		// Profiles written before character creation existed already have an accepted appearance.
		String confirmed = data.getProperty("appearance.confirmed", "true");
		if (!"true".equals(confirmed) && !"false".equals(confirmed))
			throw new IOException("Invalid appearance confirmation state");
		player.setCharacterCreationRequired(!Boolean.parseBoolean(confirmed));
		String controls = data.getProperty("tutorial.controls.pending", "false");
		if (!"true".equals(controls) && !"false".equals(controls))
			throw new IOException("Invalid controls tutorial state");
		player.setControlsTutorialPending(!player.isCharacterCreationRequired() && Boolean.parseBoolean(controls));
		NewCharacterSpawn.restorePendingFacing(player);
		SkillSet skills = player.getSkillSet();
		if (integer(data, "skills.count", 0, 100) != skills.size())
			throw new IOException("Saved skill count mismatch");
		for (int i = 0; i < skills.size(); i++)
			skills.loadSkill(i, integer(data, "skill." + i + ".level", 0, 65535),
					integer(data, "skill." + i + ".maximum", 1, 65535),
					decimal(data, "skill." + i + ".xp", 0, SkillSet.MAXIMUM_EXPERIENCE));
		for (int id : INVENTORIES) {
			String key = "inventory." + id;
			if (id == Inventory.GENERAL_STORE_FREEBIES && !data.containsKey(key + ".size")) continue;
			Inventory inventory = player.getInventories().get(id);
			if (inventory == null) throw new IOException("Unavailable saved inventory " + id);
			int size = integer(data, key + ".size", 0, inventory.toArray().length);
			for (int slot = 0; slot < size; slot++) {
				String value = data.getProperty(key + ".slot." + slot);
				if (value == null) continue;
				String[] item = value.split(":", -1);
				if (item.length != 2) throw new IOException("Invalid saved item");
				int itemId = Integer.parseInt(item[0]), amount = Integer.parseInt(item[1]);
				if (itemId < 0 || itemId > 65535 || amount < 0) throw new IOException("Invalid saved item values");
				inventory.set(slot, new Item(itemId, amount));
			}
		}
		player.getStatus().defaults();
		for (String key : data.stringPropertyNames()) {
			if (key.startsWith("varp.")) {
				int id = Integer.parseInt(key.substring(5));
				if (id < 0 || id >= 2000) throw new IOException("Invalid saved player variable");
				player.getStatus().setVar(id, Integer.parseInt(data.getProperty(key)));
			}
		}
		return player;
	}

	private static String required(Properties data, String key) throws IOException {
		String value = data.getProperty(key);
		if (value == null) throw new IOException("Missing character property: " + key);
		return value;
	}

	private static int integer(Properties data, String key, int min, int max) throws IOException {
		int value = Integer.parseInt(required(data, key));
		if (value < min || value > max) throw new IOException("Invalid character property: " + key);
		return value;
	}

	private static double decimal(Properties data, String key, double min, double max) throws IOException {
		double value = Double.parseDouble(required(data, key));
		if (!Double.isFinite(value) || value < min || value > max)
			throw new IOException("Invalid character property: " + key);
		return value;
	}

	private static void put(Properties data, String key, Object value) {
		data.setProperty(key, String.valueOf(value));
	}
}
