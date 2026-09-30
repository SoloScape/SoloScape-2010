package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.Message;

/**
 * TODO Document
 * @author Teemu
 *
 */
public class VarBitMessage implements Message {
	private final int varBitId, set;

	public VarBitMessage(int varBitId, int set) {
		this.varBitId = varBitId;
		this.set = set;
	}

	public int getVarBitId() {
		return varBitId;
	}

	public int getValue() {
		return set;
	}
}
