package net.scapeemulator.game.msg.interf.child;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.CachedMessage;

public class PlayerOnInterfaceMessage extends CachedMessage {
	private final int playerId, pointer, type;

	public PlayerOnInterfaceMessage(int interId, int childId) {
		pointer = (interId << 16) | childId;
		playerId = -1;
		type = 0;
	}

	public PlayerOnInterfaceMessage(Player player, int interId, int childId, int type) {
		pointer = (interId << 16) | childId;
		playerId = player.getId();
		this.type = type;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getPointer() {
		return pointer;
	}

	public int getType() {
		return type;
	}
}
