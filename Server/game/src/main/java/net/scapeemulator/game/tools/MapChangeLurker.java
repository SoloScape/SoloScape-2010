package net.scapeemulator.game.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.FileStore;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.ReferenceTable.Entry;
import net.scapeemulator.cache.util.ByteBufferUtils;
import net.scapeemulator.cache.util.StringUtils;
import net.scapeemulator.game.model.GameMapObject;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.map.MapArea;
import net.scapeemulator.game.model.map.Tile;
import net.scapeemulator.game.util.LandscapeKeyTable;

public class MapChangeLurker {
	public static List<Integer> targets = new ArrayList<>();
	private static final Logger logger = LoggerFactory.getLogger(MapChangeLurker.class);
	private static final String compareTargetRoot = "./613/";// Newer
	private static final String compareSourceRoot = "./562/";// "../resources/";//
																// Older
	private final Cache source, target;
	private final LandscapeKeyTable src, trgt;
	private final ReferenceTable srcReftable, trgtRefTable;

	static {
		// targets.add(9782);
		// targets.add(10292);
		targets.add(10548);
	}

	public static void main(String[] args) throws IOException {
		logger.info("Starting ReScape map change lurker!");
		Cache source = new Cache(FileStore.open(compareSourceRoot + "cache"));
		Cache target = new Cache(FileStore.open(compareTargetRoot + "cache"));
		LandscapeKeyTable src = LandscapeKeyTable.open(compareSourceRoot + "xtea/");
		// compareTargetRoot + "landscape-keys"
		LandscapeKeyTable trgt = LandscapeKeyTable.open("./613/xtea/");// "./data/landscape-keys");
		new MapChangeLurker(source, target, src, trgt);
	}

	public MapChangeLurker(Cache source, Cache target, LandscapeKeyTable src, LandscapeKeyTable trgt)
			throws IOException {
		this.source = source;
		this.target = target;
		this.src = src;
		this.trgt = trgt;
		srcReftable = ReferenceTable.decode(Container.decode(source.getStore().read(255, 5)).getData());
		trgtRefTable = ReferenceTable.decode(Container.decode(target.getStore().read(255, 5)).getData());
		work();
	}

	private void work() {
		for (Integer target : targets) {
			int x = target >> 8;
			int y = target & 0xff;
			int sourceId = -1, targetId = -1;
			int identif = StringUtils.hash("l" + x + "_" + y);
			for (int id = 0; id < srcReftable.capacity(); id++) {
				Entry entry = srcReftable.getEntry(id);
				if (entry == null || entry.getIdentifier() == -1) {
					continue;
				}
				if (identif == entry.getIdentifier()) {
					sourceId = id;
					break;
				}
			}
			if (sourceId != -1) {
				for (int id = 0; id < trgtRefTable.capacity(); id++) {
					Entry entry = trgtRefTable.getEntry(id);
					if (entry == null || entry.getIdentifier() == -1) {
						continue;
					}
					if (identif == entry.getIdentifier()) {
						targetId = id;
						break;
					}
				}
				if (targetId != -1) {
					try {
						compare(sourceId, targetId, x, y);
					} catch (IOException e) {
						logger.error("Failed to compare. ", e);
					}
				}
			} else {
				System.err.println("Map added afterwards");
				// logger.info("Can't compare, source doesn't exist.");
			}
		}
	}

	private void compare(int sourceId, int targetId, int x, int y) throws IOException {
		ReferenceTable.Entry source = srcReftable.getEntry(sourceId);
		ReferenceTable.Entry target = trgtRefTable.getEntry(targetId);
		// logger.info("Comparing reference table differences...");
		if (sourceId != targetId) {
			System.err.println(
					x + " " + y + " (" + ((x << 8) | y) + ") File ids differ! " + sourceId + " -> " + targetId);
		}
		if (source.getVersion() != target.getVersion()) {
			PrintWriter pw = new PrintWriter("./differences/562-613/" + x + "_" + y + "-" + ((x << 8) | y) + ".txt");
			System.err.println(x + " " + y + " (" + ((x << 8) | y) + ") Version Difference: " + source.getVersion()
					+ " -> " + target.getVersion());
			pw.println("Version Difference: " + source.getVersion() + " -> " + target.getVersion());
			try {
				// logger.info("Starting to compare map & landscape
				// differences...");
				compareMaps(sourceId, targetId, x, y, pw);
			} catch (Exception e) {
				// logger.info("Failed " + x + " " + y + " (" + ((x << 8) | y) +
				// ")");
				// e.printStackTrace();
			}
			pw.flush();
			pw.close();
		} else {
			System.err.println(x + " " + y + " (" + ((x << 8) | y) + ") No Version difference!");
		}
	}

	private void compareMaps(int sourceId, int targetId, int x, int y, PrintWriter pw) throws IOException {
		ByteBuffer srcRaw = getContainer(source, sourceId, src.getKeys(x, y));
		ByteBuffer trgtRaw = getContainer(target, targetId, trgt.getKeys(x, y));
		MapArea source = new MapArea(x, y, new Tile[4][64][64]);
		MapArea target = new MapArea(x, y, new Tile[4][64][64]);
		if (decodeLandscape(source, x, y, srcRaw)) {
			if (decodeLandscape(target, x, y, trgtRaw)) {
				compareLandscape(source, target, pw);
			}
		}
	}

	private void compareLandscape(MapArea source, MapArea target, PrintWriter pw) {
		Iterator<GameMapObject> srcIter = source.getObjects().iterator();
		while (srcIter.hasNext()) {
			GameMapObject srcNext = srcIter.next();
			Iterator<GameMapObject> trgtIter = target.getObjects().iterator();
			while (trgtIter.hasNext()) {
				GameMapObject trgtNext = trgtIter.next();
				if (trgtNext.exact(srcNext)) {
					srcIter.remove();
					trgtIter.remove();
					break;
				}
			}
		}
		Object[] sources = source.getObjects().toArray();
		Object[] targets = target.getObjects().toArray();
		if (sources.length != 0 || targets.length != 0) {
			logger.info("Compared area " + source.getX() + " " + source.getY() + " and found differences!");
			System.err.println("Printing different objects: ");
			pw.println("Compared area " + source.getX() + " " + source.getY() + " and found differences!");
			pw.println("Printing different objects: ");
		}
		if (sources.length != 0) {
			pw.println("----------SOURCE----------");
			pw.println(Arrays.toString(sources).replaceAll(", GameMapObject", "\n GameMapObject"));
			System.err.println("----------SOURCE----------");
			System.err.println(Arrays.toString(sources).replaceAll(", GameMapObject", "\n GameMapObject"));
		}
		if (targets.length != 0) {
			pw.println("----------TARGET----------");
			pw.println(Arrays.toString(targets).replaceAll(", GameMapObject", "\n GameMapObject"));
			System.err.println("----------TARGET----------");
			System.err.println(Arrays.toString(targets).replaceAll(", GameMapObject", "\n GameMapObject"));
		}
	}

	public static synchronized boolean decodeLandscape(MapArea area, int xArea, int yArea, ByteBuffer buffer) {
		int id = -1;
		int deltaId;

		while ((deltaId = ByteBufferUtils.getSmart(buffer)) != 0) {
			id += deltaId;

			int pos = 0;
			int deltaPos;

			while ((deltaPos = ByteBufferUtils.getSmart(buffer)) != 0) {
				pos += deltaPos - 1;

				int localX = (pos >> 6) & 0x3F;
				int localY = pos & 0x3F;
				int height = (pos >> 12) & 0x3;

				int typrot = buffer.get() & 0xFF;

				Position position = new Position((xArea << 6) + localX, (yArea << 6) + localY, height);
				area.getObjects().add(new GameMapObject(id, position, typrot));
			}
		}
		return true;
	}

	private static ByteBuffer getContainer(Cache cache, int fileId, int[] keys) throws IOException {
		ByteBuffer buffer = cache.getStore().read(5, fileId);
		if (keys == null)
			return Container.decode(buffer).getData();
		return Container.decode(buffer, keys).getData();
	}

	public static Logger getLogger() {
		return logger;
	}

	public static String getComparesourceroot() {
		return compareSourceRoot;
	}

	public static String getComparetargetroot() {
		return compareTargetRoot;
	}

	public Cache getSource() {
		return source;
	}

	public Cache getTarget() {
		return target;
	}

	public LandscapeKeyTable getSrc() {
		return src;
	}

	public LandscapeKeyTable getTrgt() {
		return trgt;
	}

	public ReferenceTable getSrcReftable() {
		return srcReftable;
	}

	public ReferenceTable getTrgtRefTable() {
		return trgtRefTable;
	}
}
