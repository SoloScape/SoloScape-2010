package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.Message;

public final class CommandMessage implements Message {

	private final String command;

	public CommandMessage(String command) {
		this.command = command;
	}

	public String getCommand() {
		return command;
	}

}
