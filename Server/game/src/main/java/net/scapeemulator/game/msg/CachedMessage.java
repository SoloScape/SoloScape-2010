package net.scapeemulator.game.msg;

import net.scapeemulator.api.message.Message;

public abstract class CachedMessage implements Message {
	private int sequenceNumber = 0;

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}
