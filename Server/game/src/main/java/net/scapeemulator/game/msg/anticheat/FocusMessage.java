package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.Message;

public final class FocusMessage implements Message {

	private final boolean focused;

	public FocusMessage(boolean focused) {
		this.focused = focused;
	}

	public boolean isFocused() {
		return focused;
	}

}
