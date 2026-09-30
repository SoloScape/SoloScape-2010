package net.scapeemulator.game.model.mob;

import net.scapeemulator.game.model.entity.Position;

public enum Direction {
	NONE(-1, 0, 0), SOUTH_WEST(5, -1, -1), SOUTH(6, 0, -1), SOUTH_EAST(7, 1, -1), WEST(3, -1, 0), EAST(4, 1,
			0), NORTH_WEST(0, -1, 1), NORTH(1, 0, 1), NORTH_EAST(2, 1, 1);

	private final int intValue;
	private final int xChange, yChange;

	private Direction(int intValue, int xChange, int yChange) {
		this.intValue = intValue;
		this.xChange = xChange;
		this.yChange = yChange;
	}

	public int toInteger() {
		return intValue;
	}

	public static Direction between(Position cur, Position next) {
		int deltaX = next.getX() - cur.getX();
		int deltaY = next.getY() - cur.getY();

		if (deltaY == 1) {
			if (deltaX == 1)
				return NORTH_EAST;
			else if (deltaX == 0)
				return NORTH;
			else if (deltaX == -1)
				return NORTH_WEST;
		} else if (deltaY == -1) {
			if (deltaX == 1)
				return SOUTH_EAST;
			else if (deltaX == 0)
				return SOUTH;
			else if (deltaX == -1)
				return SOUTH_WEST;
		} else if (deltaY == 0) {
			if (deltaX == 1)
				return EAST;
			else if (deltaX == 0)
				return NONE;
			else if (deltaX == -1)
				return WEST;
		}

		throw new IllegalArgumentException(deltaX + " " + deltaY);
	}

	public int xChange() {
		return xChange;
	}

	public int yChange() {
		return yChange;
	}
}
