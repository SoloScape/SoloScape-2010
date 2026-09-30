package net.scapeemulator.game.model.entity;

public final class Position {
	private final int x, y, height;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
		this.height = 0;
	}

	public Position(int x, int y, int height) {
		this.x = x;
		this.y = y;
		this.height = height & 0x3;
	}

	public String simpleString() {
		return "x: " + x + " y: " + y + " z:" + height;
	}

	@Override
	public String toString() {
		return "Position [x=" + x + ", y=" + y + ", height=" + height + "/ areaX=" + getAreaX() + ", areaY="
				+ getAreaY() + ", areaLocalX=" + getAreaLocalX() + ", areaLocalY=" + getAreaLocalY() + "]";
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getAreaY() {
		return y >> 6;
	}

	public int getAreaX() {
		return x >> 6;
	}

	public int getAreaLocalX() {
		return x - (getAreaX() << 6);
	}

	public int getAreaLocalY() {
		return y - (getAreaY() << 6);
	}

	public int getChunkX() {
		return x >> 3;
	}

	public int getChunkY() {
		return y >> 3;
	}

	public int getHeight() {
		return height;
	}

	public boolean isWithinDistance(Position position) {
		int deltaX = position.getX() - x;
		int deltaY = position.getY() - y;
		return deltaX >= -16 && deltaX <= 15 && deltaY >= -16 && deltaY <= 15;
	}

	public int toPackedInt() {
		return (height << 28) | (x << 14) | y;
	}

	public int toAreaInt() {
		int xArea = getAreaX();
		int yArea = getAreaY();
		return height << 16 | xArea << 8 | yArea;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (height != other.height)
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
