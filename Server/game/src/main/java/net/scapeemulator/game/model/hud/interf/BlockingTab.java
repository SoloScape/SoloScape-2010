package net.scapeemulator.game.model.hud.interf;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public abstract class BlockingTab extends Interface {

	public BlockingTab(int id, InterfacePersistence persistence) {
		super(id, persistence);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getBlockingTabRoot();
	}
}
