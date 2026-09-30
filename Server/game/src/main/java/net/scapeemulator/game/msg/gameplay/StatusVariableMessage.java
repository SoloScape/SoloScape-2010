package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.Message;

public final class StatusVariableMessage implements Message {

	private final int id, value;

	public StatusVariableMessage(int id, int value) {
		this.id = id;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

}
