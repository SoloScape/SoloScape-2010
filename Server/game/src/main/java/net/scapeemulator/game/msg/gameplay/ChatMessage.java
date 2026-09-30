package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.Message;

public final class ChatMessage implements Message {

	private final int color, effects;
	private final String text;

	public ChatMessage(int color, int effects, String text) {
		this.color = color;
		this.effects = effects;
		this.text = text;
	}

	public int getColor() {
		return color;
	}

	public int getEffects() {
		return effects;
	}

	public String getText() {
		return text;
	}

}
