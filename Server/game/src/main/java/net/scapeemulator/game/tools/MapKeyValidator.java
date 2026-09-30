package net.scapeemulator.game.tools;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.ReferenceTable.Entry;
import net.scapeemulator.cache.util.StringUtils;
import net.scapeemulator.game.cache.MapDataDecoder;
import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.model.map.MapArea;
import net.scapeemulator.game.util.LandscapeKeyTable;

public class MapKeyValidator {
	private static java.util.Map<String, Integer> table = new HashMap<>();
	private static int failed;
	private static int succesful;
	private static final Logger logger = LoggerFactory.getLogger(MapKeyValidator.class);
	private static String directory = "./data/landscape-keys", cacheDirectory = "../resources/cache",
			missingFile = "Missing Areas.txt";
	private static ReferenceTable rt;
	private static List<String> succesfull = new ArrayList<>();
	private static Cache cache;

	private static PrintWriter out;

	// Failed 148!!! out of 1228 total
	public static void main(String[] args) throws IOException {
		// directory = "./562/xtea/";
		// cacheDirectory ="./562/cache/";
		// missingFile = "./562/missing.txt";
		directory = "./xteacheck/";
		validate(directory, cacheDirectory, missingFile);
	}

	private static void validate(String directory, String cacheDirectory, String missingFile) throws IOException {
		out = new PrintWriter(missingFile);
		LandscapeKeyTable landscapeKeyTable = LandscapeKeyTable.open(directory);
		/* load game cache */
		cache = new Cache(FileStore.open(cacheDirectory));
		rt = ReferenceTable.decode(Container.decode(cache.getStore().read(255, 5)).getData());
		init(cache, landscapeKeyTable);
	}

	public static void init(Cache cache, LandscapeKeyTable keyTable) throws IOException {
		ReferenceTable rt = ReferenceTable.decode(Container.decode(cache.getStore().read(255, 5)).getData());

		long time = System.currentTimeMillis();
		Map<Integer, String> hashes = new HashMap<>();
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				String mapFile = "m" + x + "_" + y;
				String landscapeFile = "l" + x + "_" + y;
				String npcFile = "n" + x + "_" + y;
				int mapIdentifier = StringUtils.hash(mapFile);
				int npcIdentifier = StringUtils.hash(npcFile);
				int landscapeIdentifier = StringUtils.hash(landscapeFile);
				hashes.put(mapIdentifier, mapFile);
				hashes.put(npcIdentifier, npcFile);
				hashes.put(landscapeIdentifier, landscapeFile);
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
		for (int x = 0; x < 256; x++) {
			for (int y = 0; y < 256; y++) {
				String map = x + "_" + y;
				if (table.containsKey("l" + map)) {
					int[] keys = keyTable.getKeys(x, y);
					int fileId = table.remove("l" + map);
					try {
						getContainer(cache, fileId, keys);
						succesful++;
						succesfull.add(getKeyset(y | (x << 8)).getName());
					} catch (Exception e) {
						printkeys(x, y, fileId);
						failed++;
					}
				}
			}
		}
		logger.info("Removing invalid files..");

		int invalid = 0;
		for (File file : new File(directory).listFiles()) {
			if (!succesfull.contains(file.getName())) {
				invalid++;
				file.delete();
			}
		}
		logger.info("Validated maps! Took total of " + (System.currentTimeMillis() - time) + "ms. Failed/Succesful: "
				+ failed + "/" + succesful + ". Invalid keys removed: " + invalid);
		out.flush();
		out.close();
		SceneryDefinition.init(cache);
//		MapChangeLurker.main(new String[] {});
	}

	private static void printkeys(int x, int y, int fileId) {
		int reg = y | (x << 8);
		File keyset = getKeyset(reg);
		if (keyset.exists()) {
			System.out.println("Invalid keyset! " + x + " " + y + " " + reg + " " + fileId + " " + rt.getEntry(fileId));
		} else {
			// System.out.println("Missing keyset: " + x + " " + y + " " + reg +
			// " " + fileId + " " + rt.getEntry(fileId));
		}
		MapChangeLurker.targets.add(reg);
		out.println("Missing keyset: " + x + " " + y + " " + reg + " " + fileId + " " + rt.getEntry(fileId));
		if (directory.equalsIgnoreCase("./data/landscape-keys") && new File("./xteacheck/" + reg + ".txt").exists()) {
			System.out.println("WE HAVE A WORKING ONE!!!!" + x + " " + y + " " + reg);
		}
	}

	private static File getKeyset(int reg) {
		return new File(new File(directory).getAbsolutePath(), reg + ".txt");
	}

	private static ByteBuffer getContainer(Cache cache, int fileId, int[] keys) throws IOException {
		ByteBuffer buffer = cache.getStore().read(5, fileId);
		return Container.decode(buffer, keys).getData();
	}

	public static MapArea readMap(Cache cache, int x, int y, int id) throws IOException {
		ByteBuffer buffer = cache.read(5, id).getData();
		return MapDataDecoder.decodeMap(x, y, buffer);
	}

}
