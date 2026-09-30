package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class Chat extends Interface {
	public static final Interface INSTANCE = new Chat();

	public Chat() {
		super(137, InterfacePersistence.PERSISTENT);
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return 0;
	}
}
