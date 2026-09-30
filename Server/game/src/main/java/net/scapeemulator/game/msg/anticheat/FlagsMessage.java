package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.Message;

public final class FlagsMessage implements Message {

	private final int flags;

	public FlagsMessage(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}

}
