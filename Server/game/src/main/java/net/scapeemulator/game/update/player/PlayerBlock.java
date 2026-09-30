package net.scapeemulator.game.update.player;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;

public abstract class PlayerBlock {
	private final int flag;

	public PlayerBlock(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public abstract void encode(PlayerUpdateMessage message, PacketBuilder builder);

}
