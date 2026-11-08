package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.Message;

public final class NpcOptionTwoMessage implements Message {
	private final int npcIndex;
	private final boolean running;

	public NpcOptionTwoMessage(int npcIndex, boolean running) {
		this.npcIndex = npcIndex;
		this.running = running;
	}

	public int getNpcIndex() {
		return npcIndex;
	}

	public boolean isRunning() {
		return running;
	}
}
