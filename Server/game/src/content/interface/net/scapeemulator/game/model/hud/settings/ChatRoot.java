package net.scapeemulator.game.model.hud.settings;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class ChatRoot extends Interface {
	public static final Interface INSTANCE = new ChatRoot();

	public ChatRoot() {
		super(752, InterfacePersistence.PERSISTENT);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getChatRoot();
	}
}
