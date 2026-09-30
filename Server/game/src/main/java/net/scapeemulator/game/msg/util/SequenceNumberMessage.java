package net.scapeemulator.game.msg.util;

import net.scapeemulator.game.msg.CachedMessage;

public final class SequenceNumberMessage extends CachedMessage {

	public SequenceNumberMessage(int sequenceNumber) {
		setSequenceNumber(sequenceNumber);
	}
}
