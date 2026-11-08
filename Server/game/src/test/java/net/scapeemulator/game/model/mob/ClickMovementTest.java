package net.scapeemulator.game.model.mob;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.map.WorldMap;
import net.scapeemulator.game.model.pathfinder.PathFinder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.area.WalkMessage;
import net.scapeemulator.game.msg.area.WalkMessageHandler;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.junit.*;

import java.io.File;

import static org.junit.Assert.*;

public class ClickMovementTest {
	private int[][] flags;
	private int[][] saved;

	@BeforeClass
	public static void loadCollision() throws Exception {
		File server = new File("Server").isDirectory() ? new File("Server") : new File("..");
		try (Cache cache = new Cache(FileStore.open(new File(server, "cache").getPath()))) {
			SceneryDefinition.init(cache);
			WorldMap.init(cache, LandscapeKeyTable.open(new File(server, "game/data/landscape-keys")));
		}
	}

	@Before
	public void createEnclosedTestArea() {
		flags = WorldMap.getRegion(50, 53).getCollisionMaps()[0].getFlags();
		saved = new int[9][9];
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				saved[x][y] = flags[20 + x][48 + y];
				flags[20 + x][48 + y] = x == 0 || y == 0 || x == 8 || y == 8 ? 0x40000000 : 0;
			}
		}
	}

	@After
	public void restoreCollision() {
		for (int x = 0; x < 9; x++)
			for (int y = 0; y < 9; y++)
				flags[20 + x][48 + y] = saved[x][y];
	}

	@Test
	public void blockedGroundAndMinimapClicksChooseNearestTileWithShortestRoute() {
		flags[26][52] = 0x40000000; // (3226, 3444)
		for (boolean minimap : new boolean[] { false, true }) {
			for (boolean running : new boolean[] { false, true }) {
				Player player = playerAt(3222, 3444);
				click(player, 3226, 3444, running, minimap);
				assertEquals(running, player.getMovement().isRunningQueue());
				finishRoute(player);
				assertEquals(new Position(3225, 3444), player.getPosition());
			}
		}
	}

	@Test
	public void unreachableGroundAcrossWallStopsOnReachableSide() {
		for (int y = 49; y <= 55; y++)
			flags[24][y] = 0x40000000;
		Player player = playerAt(3222, 3444);
		click(player, 3226, 3444, false, false);
		finishRoute(player);
		assertEquals(new Position(3223, 3444), player.getPosition());
	}

	@Test
	public void alreadyOnNearestTileStaysPutAndCancelsOldRoute() {
		flags[26][52] = 0x40000000;
		Player player = playerAt(3225, 3444);
		click(player, 3222, 3444, false, false);
		click(player, 3226, 3444, false, false);
		finishRoute(player);
		assertEquals(new Position(3225, 3444), player.getPosition());
	}

	@Test
	public void reachableClickStillReachesExactDestination() {
		Player player = playerAt(3222, 3444);
		click(player, 3226, 3444, false, false);
		finishRoute(player);
		assertEquals(new Position(3226, 3444), player.getPosition());
	}

	@Test
	public void strictPathRequestStillRejectsBlockedDestination() {
		flags[26][52] = 0x40000000;
		Player player = playerAt(3222, 3444);
		PathFinder.findPath(player, 3226, 3444, 1, 1, false, false);
		finishRoute(player);
		assertEquals(new Position(3222, 3444), player.getPosition());
	}

	@Test
	public void varrockWestBankRegionIsLoadedAndReachableInBothDirections() {
		assertEquals(49, WorldMap.getRegion(49, 53).getX());
		Player player = playerAt(3210, 3424);
		click(player, 3185, 3436, false, false);
		finishRoute(player);
		assertEquals(new Position(3185, 3436), player.getPosition());
		click(player, 3210, 3424, true, true);
		finishRoute(player);
		assertEquals(new Position(3210, 3424), player.getPosition());
	}

	@Test
	public void fountainClickApproachesBasinWithoutEnteringIt() {
		Player player = playerAt(3211, 3424);
		click(player, 3211, 3427, false, false);
		finishRoute(player);
		assertEquals(new Position(3212, 3427), player.getPosition());
	}

	private static Player playerAt(int x, int y) {
		Player player = new Player();
		player.setPosition(new Position(x, y));
		return player;
	}

	private static void click(Player player, int x, int y, boolean running, boolean minimap) {
		new WalkMessageHandler().handle(player, new WalkMessage(x, y, running, minimap));
	}

	private static void finishRoute(Player player) {
		for (int tick = 0; tick < 30; tick++)
			player.getMovement().tick();
	}
}
