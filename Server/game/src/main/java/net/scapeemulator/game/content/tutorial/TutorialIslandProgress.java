package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class TutorialIslandProgress extends Interface {
	public static final Interface INSTANCE = new TutorialIslandProgress();

	public TutorialIslandProgress() {
		super(371, InterfacePersistence.PERSISTENT);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getOverChatboxRoot();
	}
}
