package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class ResizedHUD extends Interface {
	public static final Interface INSTANCE = new ResizedHUD();

	public ResizedHUD() {
		super(746, InterfacePersistence.PERSISTENT);
		addChild(new WorldmapButton(174));
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return DisplayMode.FULLSCREEN_INTERFACE;
	}
}
