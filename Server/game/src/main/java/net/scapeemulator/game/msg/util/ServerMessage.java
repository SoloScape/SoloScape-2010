package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.Message;

public final class ServerMessage implements Message {

	private final String text;
	private final int index;

	public ServerMessage(String text) {
		this.text = text;
		index = 0;
	}

	public ServerMessage(String text, int index) {
		this.text = text;
		this.index = index;
	}

	public String getText() {
		return text;
	}

	public int getIndex() {
		return index;
	}
}
