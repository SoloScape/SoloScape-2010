package net.scapeemulator.game.model.hud.settings;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class ChatSettings extends Interface {
	public static final Interface INSTANCE = new ChatSettings();

	public ChatSettings() {
		super(751, InterfacePersistence.PERSISTENT);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getChatOpRoot();
	}
}
