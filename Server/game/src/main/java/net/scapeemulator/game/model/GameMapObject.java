package net.scapeemulator.game.model;

import net.scapeemulator.game.cache.SceneryDefinition;
import net.scapeemulator.game.model.entity.Entity;
import net.scapeemulator.game.model.entity.Position;

public class GameMapObject extends Entity {
	public static final int TYPE_CONVERSION[] = { 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3 };
	private final int id;
	protected final byte typrot;

	public GameMapObject(int id, Position position, int typrot) {
		this.id = id;
		this.position = position;
		this.typrot = (byte) typrot;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + typrot;
		return result;
	}

	@Override
	public String toString() {
		return "GameMapObject [id=" + id + name() + ", typrot=" + typrot + ", position=" + position.simpleString()
				+ "]";
	}

	private String name() {
		if (SceneryDefinition.forId(id) != null) {
			return " " + SceneryDefinition.forId(id).getName() + " ";
		}
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GameMapObject))
			return false;
		GameMapObject other = (GameMapObject) obj;
		if (TYPE_CONVERSION[other.getType()] != TYPE_CONVERSION[getType()])
			return false;
		return true;
	}

	public boolean exact(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameMapObject other = (GameMapObject) obj;
		if (id != other.id)
			return false;
		if (typrot != other.typrot)
			return false;
		if (!position.equals(other.position))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return typrot >> 2;
	}

	public int getRotation() {
		return typrot & 0x3;
	}

	public int getTypRot() {
		return typrot;
	}

	public Number getLoc() {
		return null;
	}
}
