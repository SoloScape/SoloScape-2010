package net.scapeemulator.game.model.map;

import java.nio.ByteBuffer;

public class Tile {
	public static final int FLAG_CLIP = 0x1;
	public static final int FLAG_BRIDGE = 0x2;
	// peterbjornx's refactor suggests a remove roof flag, but is wrong ?
	// public static final int FLAG_ = 0x4; ?
	// public static final int FLAG_ = 0x8; zero logic height ?
	// public static final int FLAG_ = 0x10; seems to make it ignored ?
	public static final Tile DEFAULT = new Tile();

	static {
		DEFAULT.setFlags((byte) FLAG_CLIP);
	}

	// private int height;
	// private int overlay;
	// private int underlay;
	// private int shape;
	// private int shapeRotation;
	private byte flags;

	// public int getHeight() {
	// return height;
	// }
	//
	// public int getOverlay() {
	// return overlay;
	// }
	//
	// public int getUnderlay() {
	// return underlay;
	// }
	//
	// public int getShape() {
	// return shape;
	// }
	//
	// public int getShapeRotation() {
	// return shapeRotation;
	// }

	public int getFlags() {
		return flags;
	}

	public void setFlags(byte flags) {
		this.flags = flags;
	}

	public static Tile decodeTile(Tile[][][] tiles, int x, int y, int plane, ByteBuffer buffer) {
		Tile tile = new Tile();
		for (;;) {
			int config = buffer.get() & 0xFF;
			if (config == 0) {
				// if (plane == 0)
				// tile.height = -PerlinNoise.tileHeight(x + 932731, y + 556238)
				// * 8;
				// else
				// tile.height = tiles[x][y][plane - 1].height - 240;

				return tile;
			} else if (config == 1) {
				int height = buffer.get() & 0xFF;
				if (height == 1)
					height = 0;

				// if (plane == 0)
				// tile.height = -height * 8;
				// else
				// tile.height = tiles[x][y][plane - 1].height - height * 8;

				return tile;
			} else if (config <= 49) {
				// tile.overlay =
				buffer.get();// & 0xFF;
				// tile.shape = (config - 2) / 4;
				// tile.shapeRotation = (config - 2) % 4;
			} else if (config <= 81) {
				tile.setFlags((byte) (config - 49));
			} else {
				// tile.underlay = config - 81;
			}
		}
	}

	public Tile copy() {
		Tile tile = new Tile();
		tile.setFlags(flags);
		return tile;
	}
}