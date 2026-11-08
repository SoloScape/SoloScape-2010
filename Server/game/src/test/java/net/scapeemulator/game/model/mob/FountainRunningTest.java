package net.scapeemulator.game.model.mob;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.map.WorldMap;
import net.scapeemulator.game.model.pathfinder.PathFinder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.update.player.descr.RunPlayerDescriptor;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/** Integration coverage using the same cache and collision loading as the server. */
public class FountainRunningTest {
	private static final int[] DX = { -2, -1, 0, 1, 2, -2, 2, -2, 2, -2, 2, -2, -1, 0, 1, 2 };
	private static final int[] DY = { -2, -2, -2, -2, -2, -1, -1, 0, 0, 1, 1, 2, 2, 2, 2, 2 };

	@BeforeClass
	public static void loadCollision() throws Exception {
		File server = new File("Server").isDirectory() ? new File("Server") : new File("..");
		try (Cache cache = new Cache(FileStore.open(new File(server, "data/cache").getPath()))) {
			SceneryDefinition.init(cache);
			WorldMap.init(cache, LandscapeKeyTable.open(new File(server, "game/data/landscape-keys")));
		}
	}

	@Test
	public void clientAndServerAgreeOnEveryTileAroundVarrockFountain() {
		Player player = new Player();
		player.setPosition(new Position(3209, 3425));
		player.getMovement().tick();
		player.setRunning(true);
		Position destination = new Position(3216, 3432);
		PathFinder.findPath(player, destination.getX(), destination.getY(), 1, 1, false, false);
		for (int tick = 0; tick < 30 && !player.getPosition().equals(destination); tick++) {
			player.getMovement().tick();
			Position start = player.getStartStep();
			int direction = RunPlayerDescriptor.convert(player);
			assertEquals("Observers must encode the same movement", direction, RunPlayerDescriptor.convert(player));
			if (direction != -1) {
				assertEquals("Run update must land on the server tile", player.getPosition(),
						new Position(start.getX() + DX[direction], start.getY() + DY[direction]));
			} else {
				assertNull("Cannot encode a two-step run as a single walk", player.getRunStep());
			}
		}
		assertEquals(destination, player.getPosition());
	}

	@Test
	public void cannotWalkOrRunIntoSolidFountainBasin() {
		for (boolean running : new boolean[] { false, true }) {
			Player player = new Player();
			Position start = new Position(3211, 3424);
			player.setPosition(start);
			player.setRunning(running);
			PathFinder.findPath(player, 3211, 3427, 1, 1, false, running);
			player.getMovement().tick();
			assertEquals(start, player.getPosition());
		}
	}
}
