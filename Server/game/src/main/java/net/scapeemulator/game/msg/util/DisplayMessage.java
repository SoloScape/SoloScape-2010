package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.hud.DisplayMode;

public final class DisplayMessage implements Message {
	private final DisplayMode mode;
	private final int width, height;

	public DisplayMessage(DisplayMode mode, int width, int height) {
		this.mode = mode;
		this.width = width;
		this.height = height;
	}

	public DisplayMode getMode() {
		return mode;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}
