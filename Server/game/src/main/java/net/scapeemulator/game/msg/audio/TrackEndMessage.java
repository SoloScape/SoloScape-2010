package net.scapeemulator.game.msg.audio;

import net.scapeemulator.api.message.Message;

//TODO DOC
public class TrackEndMessage implements Message {
	private final int id;

	public TrackEndMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
