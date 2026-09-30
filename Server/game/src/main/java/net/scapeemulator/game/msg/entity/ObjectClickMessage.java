package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.Message;

public final class ObjectClickMessage implements Message {

	private final int x, y, id, option;
	private final boolean runPath;

	public ObjectClickMessage(int id, int x, int y, boolean runPath, int option) {
		this.x = x;
		this.y = y;
		this.id = id;
		this.runPath = runPath;
		this.option = option;
	}

	@Override
	public String toString() {
		return "ObjectClickMessage [x=" + x + ", y=" + y + ", id=" + id + ", runPath=" + runPath + "]";
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getId() {
		return id;
	}

	public boolean runPath() {
		return runPath;
	}

	public int getOption() {
		return option;
	}
}
