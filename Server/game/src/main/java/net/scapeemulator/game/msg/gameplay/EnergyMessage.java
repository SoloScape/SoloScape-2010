package net.scapeemulator.game.msg.gameplay;

import net.scapeemulator.api.message.Message;

public final class EnergyMessage implements Message {

	private final int energy;

	public EnergyMessage(int energy) {
		this.energy = energy;
	}

	public int getEnergy() {
		return energy;
	}

}
