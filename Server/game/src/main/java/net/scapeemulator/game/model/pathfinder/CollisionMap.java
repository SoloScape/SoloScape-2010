package net.scapeemulator.game.model.pathfinder;

import net.scapeemulator.game.model.map.WorldMap;

/**
 * @author Teemuzz
 */
public class CollisionMap {
	/*
	 * Our collision bitmasks.
	 */
	private static final int CORNER_NORTHWEST = 1;
	private static final int WALL_NORTH = 2;
	private static final int CORNER_NORTHEAST = 4;
	private static final int WALL_EAST = 8;
	private static final int CORNER_SOUTHEAST = 16;
	private static final int WALL_SOUTH = 32;
	private static final int CORNER_SOUTHWEST = 64;
	private static final int WALL_WEST = 128;
	private static final int OBJECT = 256;

	private static final int TYP1_CORNER_NORTHWEST = 512;
	private static final int TYP1_WALL_NORTH = 1024;
	private static final int TYP1_CORNER_NORTHEAST = 2048;
	private static final int TYP1_WALL_EAST = 4096;
	private static final int TYP1_CORNER_SOUTHEAST = 8192;
	private static final int TYP1_WALL_SOUTH = 16384;
	private static final int TYP1_CORNER_SOUTHWEST = 32768;
	private static final int TYP1_WALL_WEST = 0x100000;
	private static final int TYP1_OBJECT = 0x20000;
	public static final int TYP1_TILE = 0x40000;

	private static final int SOLID_CORNER_NORTHWEST = 0x400000;
	private static final int SOLID_WALL_NORTH = 0x800000;
	private static final int SOLID_CORNER_NORTHEAST = 0x1000000;
	private static final int SOLID_WALL_EAST = 0x2000000;
	private static final int SOLID_CORNER_SOUTHEAST = 0x4000000;
	private static final int SOLID_WALL_SOUTH = 0x8000000;
	private static final int SOLID_CORNER_SOUTHWEST = 0x10000000;
	private static final int SOLID_WALL_WEST = 0x20000000;
	private static final int SOLID_OBJECT = 0x40000000;
	public static final int UNREACHABLE_TILE = 0x200000;

	private final int xArea, yArea, heightLevel;
	private int[][] flags;

	public CollisionMap(int xArea, int yArea, int plane, int width, int height) {
		this.xArea = xArea;
		this.yArea = yArea;
		this.heightLevel = plane;
		flags = new int[width][height];
	}

	// Remove all-but-border
	public void removeFlags(int x, int y) {
		flags[x][y] &= ~UNREACHABLE_TILE;
	}

	public void clearFlags(int x, int y) {
		flags[x][y] = 0;
	}

	public final void addSceneCollision(int xPos, int yPos, int sceneWidth, int sceneDepth, boolean typ1,
			boolean solid) {
		int flag = OBJECT;
		if (typ1) {
			flag |= TYP1_OBJECT;
		}
		if (solid) {
			flag |= SOLID_OBJECT;
		}
		for (int xIx = xPos; xIx < sceneWidth + xPos; xIx++) {
			for (int yIx = yPos; yIx < sceneDepth + yPos; yIx++) {
				addFlag(xIx, yIx, flag);
			}
		}
	}

	public final void removeSceneCollision(int xPos, int yPos, int sceneWidth, int sceneDepth, boolean typ1,
			boolean solid) {
		int flag = OBJECT;
		if (typ1) {
			flag |= TYP1_OBJECT;
		}
		if (solid) {
			flag |= SOLID_OBJECT;
		}
		for (int xIx = xPos; xIx < sceneWidth + xPos; xIx++) {
			for (int yIx = yPos; yIx < sceneDepth + yPos; yIx++) {
				removeFlag(xIx, yIx, flag);
			}
		}
	}

	public void addWall(int xPos, int yPos, int type, int rotation, boolean typ1, boolean solid) {
		switch (type) {
		case 0:
			switch (rotation) {
			case 0:
				addFlag(xPos, yPos, WALL_WEST);
				addFlag(xPos - 1, yPos, WALL_EAST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_WEST);
					addFlag(xPos - 1, yPos, TYP1_WALL_EAST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_WEST);
					addFlag(xPos - 1, yPos, SOLID_WALL_EAST);
				}
				break;
			case 1:
				addFlag(xPos, yPos, WALL_NORTH);
				addFlag(xPos, yPos + 1, WALL_SOUTH);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_NORTH);
					addFlag(xPos, yPos + 1, TYP1_WALL_SOUTH);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_NORTH);
					addFlag(xPos, yPos + 1, SOLID_WALL_SOUTH);
				}
				break;
			case 2:
				addFlag(xPos, yPos, WALL_EAST);
				addFlag(xPos + 1, yPos, WALL_WEST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_EAST);
					addFlag(xPos + 1, yPos, TYP1_WALL_WEST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_EAST);
					addFlag(xPos + 1, yPos, SOLID_WALL_WEST);
				}
				break;
			case 3:
				addFlag(xPos, yPos, WALL_SOUTH);
				addFlag(xPos, yPos - 1, WALL_NORTH);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_SOUTH);
					addFlag(xPos, yPos - 1, TYP1_WALL_NORTH);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_SOUTH);
					addFlag(xPos, yPos - 1, SOLID_WALL_NORTH);
				}
				break;
			}
			break;
		case 1:
		case 3:
			switch (rotation) {
			case 0:
				addFlag(xPos, yPos, CORNER_NORTHWEST);
				addFlag(xPos - 1, yPos + 1, CORNER_SOUTHEAST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_CORNER_NORTHWEST);
					addFlag(xPos - 1, yPos + 1, TYP1_CORNER_SOUTHEAST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_CORNER_NORTHWEST);
					addFlag(xPos - 1, yPos + 1, SOLID_CORNER_SOUTHEAST);
				}
				break;
			case 1:
				addFlag(xPos, yPos, CORNER_NORTHEAST);
				addFlag(xPos + 1, yPos + 1, CORNER_SOUTHWEST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_CORNER_NORTHEAST);
					addFlag(xPos + 1, yPos + 1, TYP1_CORNER_SOUTHWEST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_CORNER_NORTHEAST);
					addFlag(xPos + 1, yPos + 1, SOLID_CORNER_SOUTHWEST);
				}
				break;
			case 2:
				addFlag(xPos, yPos, CORNER_SOUTHEAST);
				addFlag(xPos + 1, yPos - 1, CORNER_NORTHWEST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_CORNER_SOUTHEAST);
					addFlag(xPos + 1, yPos - 1, TYP1_CORNER_NORTHWEST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_CORNER_SOUTHEAST);
					addFlag(xPos + 1, yPos - 1, SOLID_CORNER_NORTHWEST);
				}
				break;
			case 3:
				addFlag(xPos, yPos, CORNER_SOUTHWEST);
				addFlag(xPos - 1, yPos - 1, CORNER_NORTHEAST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_CORNER_SOUTHWEST);
					addFlag(xPos - 1, yPos - 1, TYP1_CORNER_NORTHEAST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_CORNER_SOUTHWEST);
					addFlag(xPos - 1, yPos - 1, SOLID_CORNER_NORTHEAST);
				}
				break;
			}
			break;
		case 2:
			switch (rotation) {
			case 0:
				addFlag(xPos, yPos, WALL_WEST | WALL_NORTH);
				addFlag(xPos - 1, yPos, WALL_EAST);
				addFlag(xPos, yPos + 1, WALL_SOUTH);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_WEST | TYP1_WALL_NORTH);
					addFlag(xPos - 1, yPos, TYP1_WALL_EAST);
					addFlag(xPos, yPos + 1, TYP1_WALL_SOUTH);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_WEST | SOLID_WALL_NORTH);
					addFlag(xPos - 1, yPos, SOLID_WALL_EAST);
					addFlag(xPos, yPos + 1, SOLID_WALL_SOUTH);
				}
				break;
			case 1:
				addFlag(xPos, yPos, WALL_NORTH | WALL_EAST);
				addFlag(xPos, yPos + 1, WALL_SOUTH);
				addFlag(xPos + 1, yPos, WALL_WEST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_NORTH | TYP1_WALL_EAST);
					addFlag(xPos, yPos + 1, TYP1_WALL_SOUTH);
					addFlag(xPos + 1, yPos, TYP1_WALL_WEST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_NORTH | SOLID_WALL_EAST);
					addFlag(xPos, yPos + 1, SOLID_WALL_SOUTH);
					addFlag(xPos + 1, yPos, SOLID_WALL_WEST);
				}
				break;
			case 2:
				addFlag(xPos, yPos, WALL_SOUTH | WALL_EAST);
				addFlag(xPos + 1, yPos, WALL_WEST);
				addFlag(xPos, yPos + -1, WALL_NORTH);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_SOUTH | TYP1_WALL_EAST);
					addFlag(xPos + 1, yPos, TYP1_WALL_WEST);
					addFlag(xPos, yPos + -1, TYP1_WALL_NORTH);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_SOUTH | SOLID_WALL_EAST);
					addFlag(xPos + 1, yPos, SOLID_WALL_WEST);
					addFlag(xPos, yPos + -1, SOLID_WALL_NORTH);

				}
				break;
			case 3:
				addFlag(xPos, yPos, WALL_WEST | WALL_SOUTH);
				addFlag(xPos, -1 + yPos, WALL_NORTH);
				addFlag(xPos + -1, yPos, WALL_EAST);
				if (typ1) {
					addFlag(xPos, yPos, TYP1_WALL_WEST | TYP1_WALL_SOUTH);
					addFlag(xPos, -1 + yPos, TYP1_WALL_NORTH);
					addFlag(xPos + -1, yPos, TYP1_WALL_EAST);
				}
				if (solid) {
					addFlag(xPos, yPos, SOLID_WALL_WEST | SOLID_WALL_SOUTH);
					addFlag(xPos, -1 + yPos, SOLID_WALL_NORTH);
					addFlag(xPos + -1, yPos, SOLID_WALL_EAST);

				}
				break;
			}
			break;
		}
	}

	public void addFlag(int xPos, int yPos, int flag) {
		if (xPos >= 0 && xPos < flags.length && yPos >= 0 && yPos < flags[xPos].length) {
			flags[xPos][yPos] |= flag;
		} else {
			if (xArea != -1 && yArea != -1)
				addNonLocalFlag(xPos, yPos, flag);
		}
	}

	public void removeFlag(int xPos, int yPos, int flag) {
		if (xPos >= 0 && xPos < flags.length && yPos >= 0 && yPos < flags[xPos].length) {
			flags[xPos][yPos] &= ~flag;
		} else {
			if (xArea != -1 && yArea != -1)
				removeNonLocalFlag(xPos, yPos, flag);
		}
	}

	private void removeNonLocalFlag(int xPos, int yPos, int flag) {
		int xAffecting = xArea;
		int yAffecting = yArea;
		int xTile = xPos;
		int yTile = yPos;
		if (xPos < 0) {
			xAffecting--;
			xTile = 64 + xPos;
		} else if (xPos >= flags.length) {
			xAffecting++;
			xTile = xPos - 64;
		}
		if (yPos < 0) {
			yAffecting--;
			yTile = 64 + yPos;
		} else if (yPos >= flags.length) {
			yAffecting++;
			yTile = yPos - 64;
		}
		WorldMap.getRegion(xAffecting, yAffecting).getCollisionMaps()[heightLevel].flags[xTile][yTile] &= ~flag;
	}

	private void addNonLocalFlag(int xPos, int yPos, int flag) {
		int xAffecting = xArea;
		int yAffecting = yArea;
		int xTile = xPos;
		int yTile = yPos;
		if (xPos < 0) {
			xAffecting--;
			xTile = 64 + xPos;
		} else if (xPos >= flags.length) {
			xAffecting++;
			xTile = xPos - 64;
		}
		if (yPos < 0) {
			yAffecting--;
			yTile = 64 + yPos;
		} else if (yPos >= flags.length) {
			yAffecting++;
			yTile = yPos - 64;
		}
		WorldMap.getRegion(xAffecting, yAffecting).
		getCollisionMaps()[heightLevel]
				.flags[xTile][yTile] |= flag;
	}

	public int[][] getFlags() {
		return flags;
	}
}