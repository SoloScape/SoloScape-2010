package net.scapeemulator.game.io;

import net.scapeemulator.game.conf.WorldConfiguration;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.inventory.*;
import net.scapeemulator.game.model.player.*;
import net.scapeemulator.game.model.player.var.PVariable;
import net.scapeemulator.game.net.login.LoginResponse;
import net.scapeemulator.game.net.login.LoginService;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FilePlayerSerializerTest {
	@Rule public TemporaryFolder temporary = new TemporaryFolder();

	@BeforeClass
	public static void registerInventories() {
		Inventory.addDetails(new InventoryInfo(93, "inventory", 149, 0, 28, Inventory.StackMode.STACKABLE_ONLY));
		Inventory.addDetails(new InventoryInfo(94, "equipment", 387, 28, 14, Inventory.StackMode.STACKABLE_ONLY));
		Inventory.addDetails(new InventoryInfo(95, "bank", 762, 87, 496, Inventory.StackMode.ALWAYS));
		Inventory.addDetails(new InventoryInfo(35, "samples", 620, 26, 496, Inventory.StackMode.ALWAYS));
		new PVariable.Builder<Integer>("persistence_test").id(1900).type(PVariable.Type.VARP)
				.value(0).value(1).setDefault(1).persist().register();
	}

	@Test
	public void freshSerializerRestoresTileItemsSkillsAppearanceEnergyAndPendingSettings() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		Player original = load(serializer, "Callum", "test-password");
		assertEquals(new Position(3678, 4937, 0), original.getPosition());
		assertEquals(new Position(3678, 4938, 0), original.getOrientationPosition());
		original.setPosition(new Position(3217, 3428, 2));
		original.setEnergy(37.625);
		original.getSkillSet().loadSkill(0, 72, 75, 1210421.5);
		original.getSkillSet().loadSkill(3, 51, 80, 1986068);
		original.getInventories().get(93).set(27, new Item(4151, 1));
		original.getInventories().get(94).set(3, new Item(11694, 1));
		original.getInventories().get(95).set(495, new Item(995, 1234567));
		original.getStatus().setVar(1900, 0); // Explicit zero must override the registered default.
		original.setRunning(true);
		original.setAppearance(new Appearance(Gender.FEMALE, new int[] { 45, 46, 47, 48, 49, 50, 51 },
				new int[] { 1, 2, 3, 4, 5 }));
		serializer.save(original);
		Player restored = load(new FilePlayerSerializer(directory), "callum", "test-password");
		assertEquals(original.getPosition(), restored.getPosition());
		assertNull(restored.getOrientationPosition()); // Don't reset a returning player's facing.
		assertEquals(37.625, restored.getPreciseEnergy(), 0);
		for (int i = 0; i < original.getSkillSet().size(); i++) {
			assertEquals(original.getSkillSet().getCurrentLevel(i), restored.getSkillSet().getCurrentLevel(i));
			assertEquals(original.getSkillSet().getMaximumLevel(i), restored.getSkillSet().getMaximumLevel(i));
			assertEquals(original.getSkillSet().getExperience(i), restored.getSkillSet().getExperience(i), 0);
		}
		for (int id : new int[] { 93, 94, 95, 35 })
			assertArrayEquals(original.getInventories().get(id).toArray(), restored.getInventories().get(id).toArray());
		assertTrue(restored.isRunning());
		assertEquals(0, restored.getStatus().getWaitingVar(1900));
		assertEquals(Gender.FEMALE, restored.getAppearance().getGender());
		for (int i = 0; i < 7; i++) assertEquals(original.getAppearance().getStyle(i), restored.getAppearance().getStyle(i));
		for (int i = 0; i < 5; i++) assertEquals(original.getAppearance().getColor(i), restored.getAppearance().getColor(i));
		assertEquals(original.getRights(), restored.getRights());
	}

	@Test
	public void creationResumesUntilConfirmedAndKeepsTheChosenAppearance() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		Player player = load(serializer, "newlook", "password");
		assertTrue(player.isCharacterCreationRequired());
		player.setAppearance(new Appearance(Gender.FEMALE, new int[] {697, 0, 153, 147, 68, 129, 79},
				new int[] {20, 24, 24, 0, 7}));
		serializer.save(player);
		Player resumed = load(new FilePlayerSerializer(directory), "newlook", "password");
		assertTrue(resumed.isCharacterCreationRequired());
		assertEquals(NewCharacterSpawn.POSITION, resumed.getPosition());
		assertEquals(new Position(3678, 4938, 0), resumed.getOrientationPosition());
		assertEquals(Gender.FEMALE, resumed.getAppearance().getGender());
		resumed.setCharacterCreationRequired(false);
		serializer.save(resumed);
		Player confirmed = load(new FilePlayerSerializer(directory), "newlook", "password");
		assertFalse(confirmed.isCharacterCreationRequired());
		assertEquals(NewCharacterSpawn.POSITION, confirmed.getPosition());
		assertNull(confirmed.getOrientationPosition()); // Only new/pending characters are reoriented.
		assertEquals(697, confirmed.getAppearance().getStyle(0));
		assertEquals(7, confirmed.getAppearance().getColor(4));
	}

	@Test
	public void tutorialTabUnlocksPersistWithoutAffectingOlderAccounts() throws Exception {
		Path dir = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(dir);
		Player newPlayer = load(serializer, "tabprogress", "password");
		assertTrue(newPlayer.isTutorialTabsRestricted());
		assertEquals(1 << net.scapeemulator.game.model.hud.interf.Tab.SETTINGS,
				newPlayer.getTutorialTabMask());
		newPlayer.setCharacterCreationRequired(false);
		newPlayer.setControlsTutorialPending(true);
		newPlayer.setTutorialTabState(true,
				newPlayer.getTutorialTabMask() | (1 << net.scapeemulator.game.model.hud.interf.Tab.INVENTORY));
		serializer.save(newPlayer);

		Player loaded = load(new FilePlayerSerializer(dir), "tabprogress", "password");
		assertTrue(loaded.isTutorialTabsRestricted());
		assertTrue(loaded.isTutorialTabUnlocked(net.scapeemulator.game.model.hud.interf.Tab.SETTINGS));
		assertTrue(loaded.isTutorialTabUnlocked(net.scapeemulator.game.model.hud.interf.Tab.INVENTORY));
		assertFalse(loaded.isTutorialTabUnlocked(net.scapeemulator.game.model.hud.interf.Tab.ATTACK));
		assertFalse(loaded.isCharacterCreationRequired());

		loaded.setTutorialTabState(false, (1 << net.scapeemulator.game.model.hud.interf.Tab.TAB_COUNT) - 1);
		serializer.save(loaded);
		Player completed = load(new FilePlayerSerializer(dir), "tabprogress", "password");
		assertFalse(completed.isTutorialTabsRestricted());
		assertTrue(completed.isTutorialTabUnlocked(net.scapeemulator.game.model.hud.interf.Tab.ATTACK));
	}

	@Test
	public void cameraControlsLessonIsSavedUntilThePlayerRotatesTheView() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		Player player = load(serializer, "cameranew", "password");
		assertFalse(player.isControlsTutorialPending());
		player.setCharacterCreationRequired(false);
		player.setControlsTutorialPending(true);
		serializer.save(player);

		Player resumed = load(new FilePlayerSerializer(directory), "cameranew", "password");
		assertFalse(resumed.isCharacterCreationRequired());
		assertTrue(resumed.isControlsTutorialPending());
		assertEquals(player.getPosition(), resumed.getPosition());
		resumed.setControlsTutorialPending(false);
		serializer.save(resumed);

		Player completed = load(new FilePlayerSerializer(directory), "cameranew", "password");
		assertFalse(completed.isControlsTutorialPending());
		assertFalse(completed.isCharacterCreationRequired());
	}

	@Test
	public void profilesFromBeforeCreationWasAddedKeepTheirAcceptedAppearance() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		load(serializer, "oldlook", "password");
		Path profile = directory.resolve("player-oldlook.properties");
		Properties data = new Properties();
		try (InputStream input = Files.newInputStream(profile)) { data.load(input); }
		data.remove("appearance.confirmed");
		data.remove("tutorial.tabs.restricted");
		data.remove("tutorial.tabs.mask");
		try (OutputStream output = Files.newOutputStream(profile)) { data.store(output, "legacy profile"); }
		Player legacy = load(serializer, "oldlook", "password");
		assertFalse(legacy.isCharacterCreationRequired());
		assertFalse(legacy.isTutorialTabsRestricted());
		assertTrue(legacy.isTutorialTabUnlocked(net.scapeemulator.game.model.hud.interf.Tab.INVENTORY));
		assertEquals(Appearance.DEFAULT_APPEARANCE.getStyle(0), legacy.getAppearance().getStyle(0));
	}

	@Test
	public void removedItemsStayRemovedAndReturningCharactersDoNotReceiveStarterStockAgain() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		Player player = load(serializer, "items", "password");
		player.getInventories().get(93).set(0, new Item(995, 500));
		serializer.save(player);
		player.getInventories().get(93).set(0, null);
		Item[] samples = player.getInventories().get(35).toArray();
		for (int i = 0; i < samples.length; i++) player.getInventories().get(35).set(i, null);
		serializer.save(player);
		Player restored = load(new FilePlayerSerializer(directory), "items", "password");
		assertNull(restored.getInventories().get(93).get(0));
		for (Item item : restored.getInventories().get(35).toArray()) assertNull(item);
	}

	@Test
	public void passwordIsHashedAndWrongPasswordCannotResetCharacter() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		load(serializer, "account", "secret-pass-987");
		Path profile = directory.resolve("player-account.properties");
		byte[] saved = Files.readAllBytes(profile);
		assertFalse(new String(saved, "ISO-8859-1").contains("secret-pass-987"));
		assertEquals(LoginResponse.STATUS_INVALID_PASSWORD, serializer.load("account", "wrong").getStatus());
		assertArrayEquals(saved, Files.readAllBytes(profile));
		assertEquals(LoginResponse.STATUS_OK, serializer.load("ACCOUNT", "secret-pass-987").getStatus());
		assertEquals(1, directory.toFile().listFiles().length);
	}

	@Test
	public void malformedProfilesAndUnsafeNamesAreRejectedWithoutOverwritingFiles() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		load(serializer, "broken", "password");
		Path profile = directory.resolve("player-broken.properties");
		Properties data = new Properties();
		try (InputStream in = Files.newInputStream(profile)) { data.load(in); }
		data.setProperty("skill.0.xp", "NaN");
		try (OutputStream out = Files.newOutputStream(profile)) { data.store(out, "invalid fixture"); }
		byte[] corrupt = Files.readAllBytes(profile);
		assertEquals(LoginResponse.STATUS_ERROR_LOADING_PROFILE, serializer.load("broken", "password").getStatus());
		assertArrayEquals(corrupt, Files.readAllBytes(profile));
		for (String name : new String[] { "../outside", "..", "", "a/b", "a\\b" })
			assertEquals(LoginResponse.STATUS_ERROR_LOADING_PROFILE, serializer.load(name, "password").getStatus());
		assertEquals(1, directory.toFile().listFiles().length);
	}

	@Test
	public void logoutSavesBeforeRemovalAndStopsQueuedMovement() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		Player player = load(serializer, "logout", "password");
		player.setPosition(new Position(3205, 3412, 1));
		player.getMovement().addFirstStep(new Position(3208, 3412, 1));
		World world = World.getWorld(new WorldConfiguration());
		assertTrue(world.getPlayers().add(player));
		LoginService service = new LoginService(serializer, 592);
		service.addLogoutRequest(player);
		service.addLogoutRequest(player);
		service.registerNewPlayers(world);
		assertNull(world.getPlayerByName("logout"));
		assertFalse(player.isListed());
		player.getMovement().tick();
		assertEquals(new Position(3205, 3412, 1), player.getPosition());
		assertEquals(player.getPosition(), load(new FilePlayerSerializer(directory), "logout", "password").getPosition());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void failedLogoutSaveRetainsAccountAndRetriesBeforeReleasingIt() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer disk = new FilePlayerSerializer(directory);
		Player player = load(disk, "retry", "password");
		player.setPosition(new Position(3250, 3450));
		World world = World.getWorld(new WorldConfiguration());
		assertTrue(world.getPlayers().add(player));
		PlayerSerializer flaky = new PlayerSerializer() {
			private boolean fail = true;
			@Override public SerializeResult load(String name, String pass) { return disk.load(name, pass); }
			@Override public void save(Player saved) {
				if (fail) { fail = false; throw new IllegalStateException("Simulated disk failure"); }
				disk.save(saved);
			}
		};
		LoginService service = new LoginService(flaky, 592);
		Field field = LoginService.class.getDeclaredField("activeAccounts");
		field.setAccessible(true);
		Set<String> active = (Set<String>) field.get(service);
		active.add("retry");
		try {
			service.addLogoutRequest(player);
			service.registerNewPlayers(world);
			assertSame(player, world.getPlayerByName("retry"));
			assertTrue(active.contains("retry"));
			service.registerNewPlayers(world);
			assertNull(world.getPlayerByName("retry"));
			assertFalse(active.contains("retry"));
			assertEquals(player.getPosition(), load(new FilePlayerSerializer(directory), "retry", "password").getPosition());
		} finally {
			if (player.isListed()) world.getPlayers().remove(player);
		}
	}

	@Test
	public void shutdownSavePersistsOnlinePlayers() throws Exception {
		Path directory = temporary.newFolder().toPath();
		FilePlayerSerializer serializer = new FilePlayerSerializer(directory);
		Player player = load(serializer, "shutdown", "password");
		player.setPosition(new Position(3001, 3002, 3));
		player.getInventories().get(95).set(17, new Item(995, 200));
		World world = World.getWorld(new WorldConfiguration());
		assertTrue(world.getPlayers().add(player));
		try {
			new LoginService(serializer, 592).saveAllPlayers(world);
			Player restored = load(new FilePlayerSerializer(directory), "shutdown", "password");
			assertEquals(player.getPosition(), restored.getPosition());
			assertEquals(new Item(995, 200), restored.getInventories().get(95).get(17));
		} finally { world.getPlayers().remove(player); }
	}

	@Test
	public void characterSurvivesASeparateJvmRestart() throws Exception {
		Path directory = temporary.newFolder().toPath();
		for (String phase : new String[] { "save", "load" }) {
			Path log = temporary.newFile(phase + ".log").toPath();
			String executable = System.getProperty("os.name").startsWith("Windows") ? "java.exe" : "java";
			Process process = new ProcessBuilder(new File(System.getProperty("java.home"), "bin/" + executable).getPath(),
					"-cp", System.getProperty("java.class.path"), RestartProbe.class.getName(), directory.toString(), phase)
					.redirectErrorStream(true).redirectOutput(log.toFile()).start();
			if (!process.waitFor(30, TimeUnit.SECONDS)) {
				process.destroyForcibly();
				fail("Character restart probe timed out");
			}
			assertEquals(new String(Files.readAllBytes(log), "UTF-8"), 0, process.exitValue());
		}
	}

	private static Player load(FilePlayerSerializer serializer, String username, String password) {
		PlayerSerializer.SerializeResult result = serializer.load(username, password);
		assertEquals(LoginResponse.STATUS_OK, result.getStatus());
		return result.getPlayer();
	}

	/** Separate JVMs prove that character state comes from disk rather than static memory. */
	public static final class RestartProbe {
		public static void main(String[] args) throws Exception {
			registerInventories();
			FilePlayerSerializer serializer = new FilePlayerSerializer(Paths.get(args[0]));
			Player player = load(serializer, "restart", "password");
			if (args[1].equals("save")) {
				player.setPosition(new Position(3099, 3499, 2));
				player.getInventories().get(93).set(2, new Item(995, 8123));
				player.getInventories().get(94).set(3, new Item(4151, 1));
				player.getInventories().get(95).set(100, new Item(11694, 1));
				player.getSkillSet().loadSkill(0, 55, 55, 166636.75);
				serializer.save(player);
			} else {
				assertEquals(new Position(3099, 3499, 2), player.getPosition());
				assertEquals(new Item(995, 8123), player.getInventories().get(93).get(2));
				assertEquals(new Item(4151, 1), player.getInventories().get(94).get(3));
				assertEquals(new Item(11694, 1), player.getInventories().get(95).get(100));
				assertEquals(55, player.getSkillSet().getMaximumLevel(0));
				assertEquals(166636.75, player.getSkillSet().getExperience(0), 0);
			}
		}
	}
}
