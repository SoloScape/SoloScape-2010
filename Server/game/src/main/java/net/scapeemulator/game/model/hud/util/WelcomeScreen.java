package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.child.InterfaceChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;

public class WelcomeScreen extends Interface {
	public static final Interface INSTANCE = new WelcomeScreen();

	public WelcomeScreen() {
		super(378, InterfacePersistence.TRANSIENT);
		addChild(new InterfaceChild(3, new MessageOfTheWeek(16, 7,
				"Spotted a bug? Let the developers know! You can report bugs on the rune-server project thread!")));
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return DisplayMode.FULLSCREEN_INTERFACE;
	}
}
