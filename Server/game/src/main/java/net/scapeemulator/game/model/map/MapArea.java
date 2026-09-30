package net.scapeemulator.game.model.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.model.GameMapObject;
import net.scapeemulator.game.model.pathfinder.CollisionMap;

public class MapArea {
	protected final CollisionMap[] collisionMaps;
	protected final Tile[][][] tiles;
	private final List<GameMapObject> objects = new ArrayList<>();
	private final int x, y;

	public MapArea(int x, int y, Tile[][][] tiles) {
		collisionMaps = new CollisionMap[4];
		this.tiles = tiles;
		this.x = x;
		this.y = y;
		initCollisionMaps();
	}

	protected void initCollisionMaps() {
		for (int height = 0; height < 4; height++)
			collisionMaps[height] = new CollisionMap(x, y, height, 64, 64);
	}

	protected void addCollision() {
		initCollisionMaps();
		for (int height = 0; height < 4; height++) {
			for (int x = 0; x < tiles[height].length; x++) {
				for (int y = 0; y < tiles[height].length; y++) {
					addTileCollision(x, y, height);
				}
			}
		}
		addSceneryCollision();
	}

	protected void addSceneryCollision() {
		Iterator<GameMapObject> iter = objects.iterator();
		while (iter.hasNext()) {
			GameMapObject object = iter.next();
			if (object.getId() != -1) {
				SceneryDefinition def = SceneryDefinition.forId(object.getId());
				if (def.getCollisionType() == 0)
					continue;
				addCollision(object, def, object.getPosition().getAreaLocalX(), object.getPosition().getAreaLocalY(),
						object.getPosition().getHeight(), object.getRotation());
			}
		}
	}

	protected void addTileCollision(int x, int y, int height) {
		Tile tile = getTile(x, y, height);
		int affectingHeight = height;
		if ((getTile(x, y, 1).getFlags() & Tile.FLAG_BRIDGE) != 0) {
			affectingHeight--;
		}
		if ((tile.getFlags() & Tile.FLAG_CLIP) != 0) {
			if (affectingHeight >= 0) {
				collisionMaps[affectingHeight].addFlag(x, y, CollisionMap.UNREACHABLE_TILE);// CORRECT
			}
		}
	}

	protected void addCollision(GameMapObject object, SceneryDefinition def, int xLocal, int yLocal, int height,
			int rotation) {
		if ((getTile(xLocal, yLocal, 1).getFlags() & Tile.FLAG_BRIDGE) != 0) {
			height--;
		}
		if (height >= 0) {
			int obWidth;
			int obDepth;
			if (rotation != 1 && rotation != 3) {
				obWidth = def.getWidth();
				obDepth = def.getHeight();
			} else {
				obWidth = def.getHeight();
				obDepth = def.getWidth();
			}
			int type = object.getType();
			switch (type) {
			case 9:
			case 10:
			case 11:
				if (def.getCollisionType() != 0 && collisionMaps != null) {
					collisionMaps[height].addSceneCollision(xLocal, yLocal, obWidth, obDepth, def.isTyp1(),
							!def.isWalkable());
				}
				break;
			case 22:
				if (def.getCollisionType() == 1 && collisionMaps != null) {
					collisionMaps[height].addFlag(xLocal, yLocal, CollisionMap.TYP1_TILE);
				}
				break;
			case 0:
			case 1:
			case 2:
			case 3:
				if (def.getCollisionType() != 0 && collisionMaps != null) {
					collisionMaps[height].addWall(xLocal, yLocal, type, rotation, def.isTyp1(), !def.isWalkable());
				}
				break;
			default:
				if (type >= 12 && type <= 17 || type >= 18 && type <= 21) {
					if (def.getCollisionType() != 0 && collisionMaps != null) {
						collisionMaps[height].addSceneCollision(xLocal, yLocal, obWidth, obDepth, def.isTyp1(),
								!def.isWalkable());
					}
				}
				break;
			}
		}
	}

	protected void removeCollision(GameMapObject object, SceneryDefinition def, int xLocal, int yLocal, int height,
			int rotation) {
		if ((getTile(xLocal, yLocal, 1).getFlags() & Tile.FLAG_BRIDGE) != 0) {
			height--;
		}
		if (height >= 0) {
			int obWidth;
			int obDepth;
			if (rotation != 1 && rotation != 3) {
				obWidth = def.getWidth();
				obDepth = def.getHeight();
			} else {
				obWidth = def.getHeight();
				obDepth = def.getWidth();
			}
			int type = object.getType();
			switch (type) {
			case 9:
			case 10:
			case 11:
				if (def.getCollisionType() != 0 && collisionMaps != null) {
					collisionMaps[height].removeSceneCollision(xLocal, yLocal, obWidth, obDepth, def.isTyp1(),
							!def.isWalkable());
				}
				break;
			case 22:
				if (def.getCollisionType() == 1 && collisionMaps != null) {
					collisionMaps[height].removeFlag(xLocal, yLocal, CollisionMap.TYP1_TILE);
				}
				break;
			case 0:
			case 1:
			case 2:
			case 3:
				if (def.getCollisionType() != 0 && collisionMaps != null) {
					// TODO remove wall
					// collisionMaps[height].addWall(xLocal, yLocal, type,
					// rotation, def.isTyp1(), !def.isWalkable());
				}
				break;
			default:
				if (type >= 12 && type <= 17 || type >= 18 && type <= 21) {
					if (def.getCollisionType() != 0 && collisionMaps != null) {
						collisionMaps[height].removeSceneCollision(xLocal, yLocal, obWidth, obDepth, def.isTyp1(),
								!def.isWalkable());
					}
				}
				break;
			}
		}
	}

	private Tile getTile(int x, int y, int height) {
		Tile tile = tiles[height][x][y];
		if (tile == null)
			tile = Tile.DEFAULT;
		return tile;
	}

	public CollisionMap[] getCollisionMaps() {
		return collisionMaps;
	}

	public static MapArea emptyRegion() {
		Tile[][][] tiles = new Tile[4][64][64];
		for (int height = 0; height < 4; height++) {
			for (int x = 0; x < tiles[height].length; x++) {
				for (int y = 0; y < tiles[height].length; y++) {
					tiles[height][x][y] = Tile.DEFAULT;
				}
			}
		}
		MapArea result = new MapArea(-1, -1, tiles);
		result.addCollision();
		return result;
	}

	public int deleteObject(int height, int cx, int cy, GameMapObject object) {
		return 0;
	}

	public int width() {
		return 64;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public List<GameMapObject> getObjects() {
		return objects;
	}
}