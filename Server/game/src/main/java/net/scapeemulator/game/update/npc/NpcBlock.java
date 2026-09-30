package net.scapeemulator.game.update.npc;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;

public abstract class NpcBlock {
	private final int flag;

	public NpcBlock(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public abstract void encode(NpcUpdateMessage message, PacketBuilder builder);
}
