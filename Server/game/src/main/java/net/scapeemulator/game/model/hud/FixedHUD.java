package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class FixedHUD extends Interface {
	public static final Interface INSTANCE = new FixedHUD();
	
	public FixedHUD() {
		super(548, InterfacePersistence.PERSISTENT);
		addChild(new WorldmapButton(130));
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return DisplayMode.FULLSCREEN_INTERFACE;
	}
}
