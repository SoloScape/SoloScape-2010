package net.scapeemulator.game.model.hud.orb;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class Orb extends Interface {
	private final int slot, fixedSlot;

	public Orb(int slot, int id) {
		super(id, InterfacePersistence.PERSISTENT);
		this.slot = slot;
		if (slot > 0)
			fixedSlot = slot + 1 + (slot == 3 ? 1 : 0);
		else
			fixedSlot = 0;
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		switch (mode) {
		default:
		case BACKDOOR:
		case FIXED:
			return mode.getOrbRoot() + fixedSlot;
		case FULLSCREEN:
		case RESIZED:
			return mode.getOrbRoot() + slot;
		}
	}
}
