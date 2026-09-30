package net.scapeemulator.game.model.map;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.ReferenceTable.Entry;
import net.scapeemulator.cache.util.StringUtils;
import net.scapeemulator.game.cache.MapDataDecoder;
import net.scapeemulator.game.model.FieldOfView;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.pathfinder.CollisionMap;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public final class WorldMap {
	private static java.util.Map<String, Integer> table = new HashMap<>();
	private static java.util.Map<Integer, MapArea> regions = new HashMap<>();

	private static final Logger logger = LoggerFactory.getLogger(WorldMap.class);
	private static final MapArea EMPTY_REGION = MapArea.emptyRegion();

	public static void init(Cache cache, LandscapeKeyTable keyTable) throws IOException {
		ReferenceTable rt = ReferenceTable.decode(Container.decode(cache.getStore().read(255, 5)).getData());

		long time = System.currentTimeMillis();
		Map<Integer, String> hashes = new HashMap<>();
		for (int x = 0; x < 256; x++) {
			if (x < 50)
				x = 50;
			if (x > 70)
				x = 256;
			for (int y = 0; y < 256; y++) {
				if (y < 50)
					y = 50;
				if (y > 70)
					y = 256;
				String mapFile = "m" + x + "_" + y;
				String landscapeFile = "l" + x + "_" + y;
				String npcFile = "n" + x + "_" + y;
				hashes.put(StringUtils.hash(mapFile), mapFile);
				hashes.put(StringUtils.hash(npcFile), npcFile);
				hashes.put(StringUtils.hash(landscapeFile), landscapeFile);
			}
		}
		for (int id = 0; id < rt.capacity(); id++) {
			Entry entry = rt.getEntry(id);
			if (entry == null || entry.getIdentifier() == -1) {
				continue;
			}
			if (hashes.keySet().contains(entry.getIdentifier())) {
				table.put(hashes.get(entry.getIdentifier()), id);
			}
		}
		logger.info("Parsed entry names! " + table.size() + " time: " + (System.currentTimeMillis() - time));
		int failed = 0, total = 0;
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				String map = x + "_" + y;
				if (table.containsKey("m" + map)) {
					MapArea region = WorldMap.readMap(cache, x, y, table.remove("m" + map));
					try {
						int[] keys = keyTable.getKeys(x, y);
						if (table.containsKey("l" + map))
							MapDataDecoder.decodeLandscape(region, x, y, getContainer(cache, "l" + map, keys));
						// if (table.containsKey("n" + map))
						// MapDataDecoder.decodeSpawns(region, x, y,
						// getContainer(cache, "n" + map, keys));
						total++;
					} catch (Exception e) {
						failed++;
						if (failed % 20 == 20)
							System.gc();
						logger.info("Failed to read landscape/spawn file " + x + ", " + y + ". " + e.getMessage());
					}
					regions.put((x << 8) | y, region);
				}
			}
		}
		for (MapArea region : regions.values())
			region.addCollision();
		for (int i = 0; i < 10; i++)
			System.gc();
		logger.info("Loaded MapSet! Took total of " + (System.currentTimeMillis() - time) + "ms. Failed/Total: "
				+ failed + "/" + total + ".");

	}

	private static ByteBuffer getContainer(Cache cache, String name, int[] keys) throws IOException {
		ByteBuffer buffer = cache.getStore().read(5, table.remove(name));
		return Container.decode(buffer, keys).getData();
	}

	/**
	 * Gets a area consisting of the visible chunks.
	 */
	public static CollisionMap getArea(Position position, FieldOfView fov) {
		int baseX = (position.getChunkX() - (fov.getTiles() >> 4)) << 3;
		int baseY = (position.getChunkY() - (fov.getTiles() >> 4)) << 3;
		CollisionMap collisionMap = new CollisionMap(baseX, baseY, position.getHeight(), fov.getTiles(),
				fov.getTiles());
		for (int x = 0; x < fov.getTiles(); x++) {
			for (int y = 0; y < fov.getTiles(); y++) {
				collisionMap.getFlags()[x][y] = getFlags(baseX + x, baseY + y, position.getHeight());
			}
		}
		return collisionMap;
	}

	private static int getFlags(int xPos, int yPos, int height) {
		int xa = xPos >> 6, ya = yPos >> 6;
		MapArea region = getRegion(xa, ya);
		return region.getCollisionMaps()[height].getFlags()[xPos - (xa << 6)][yPos - (ya << 6)];
	}

	public static MapArea getRegion(int xArea, int yArea) {
		MapArea region = regions.get((xArea << 8) | yArea);
		if (region == null) {
			return EMPTY_REGION;
		}
		return region;
	}

	public static MapArea readMap(Cache cache, int x, int y, int id) throws IOException {
		ByteBuffer buffer = cache.read(5, id).getData();
		return MapDataDecoder.decodeMap(x, y, buffer);
	}
}
