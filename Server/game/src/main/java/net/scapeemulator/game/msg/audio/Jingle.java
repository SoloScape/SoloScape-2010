package net.scapeemulator.game.msg.audio;

import net.scapeemulator.api.message.Message;

public class Jingle implements Message {
	private final int id, var1, volume;

	public Jingle(int id, int var1) {
		this(id, var1, 255);
	}

	public Jingle(int id, int var1, int volume) {
		this.id = id;
		this.var1 = var1;
		this.volume = volume;
	}

	public int getId() {
		return id;
	}

	public int getVar1() {
		return var1;
	}

	public int getVolume() {
		return volume;
	}
}
