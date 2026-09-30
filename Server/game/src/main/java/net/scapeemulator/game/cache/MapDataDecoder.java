package net.scapeemulator.game.cache;

import net.scapeemulator.cache.util.ByteBufferUtils;
import net.scapeemulator.game.model.GameMapObject;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.map.MapArea;
import net.scapeemulator.game.model.map.Tile;

import java.nio.ByteBuffer;

public final class MapDataDecoder {
	public static void decodeLandscape(MapArea area, int xArea, int yArea, ByteBuffer buffer) {
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
	}

	public static MapArea decodeMap(int x, int y, ByteBuffer buffer) {
		Tile[][][] tiles = new Tile[4][64][64];
		for (int plane = 0; plane < 4; plane++) {
			for (int tx = 0; tx < 64; tx++) {
				for (int ty = 0; ty < 64; ty++) {
					tiles[plane][tx][ty] = Tile.decodeTile(tiles, tx, ty, plane, buffer);
				}
			}
		}
		return new MapArea(x, y, tiles);
	}

	public static void decodeSpawns(MapArea region, int x, int y, ByteBuffer container) {

	}
}
