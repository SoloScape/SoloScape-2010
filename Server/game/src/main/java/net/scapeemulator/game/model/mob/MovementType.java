package net.scapeemulator.game.model.mob;

public enum MovementType {
	CRAWL, WALK, RUN, TELEPORT;
	public int getId() {
		if (this == TELEPORT)
			return 127;
		else
			return ordinal();
	}
}
