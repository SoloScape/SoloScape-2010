package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class ItemCreationInterface extends Interface {

	public ItemCreationInterface() {
		super(890, InterfacePersistence.TRANSIENT);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return 0;
	}
}
