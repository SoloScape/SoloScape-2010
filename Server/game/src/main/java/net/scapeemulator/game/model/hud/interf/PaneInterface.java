package net.scapeemulator.game.model.hud.interf;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class PaneInterface extends Interface {

	public PaneInterface(int id, InterfacePersistence persistence) {
		super(id, persistence);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getPaneRoot();
	}
}
