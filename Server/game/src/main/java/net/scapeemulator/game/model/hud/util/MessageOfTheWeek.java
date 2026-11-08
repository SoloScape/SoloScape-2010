package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.child.TextChild;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;

public class MessageOfTheWeek extends Interface {

	public MessageOfTheWeek(int id, int textId, String message) {
		super(id, InterfacePersistence.PERSISTENT);
		addChild(new TextChild(textId, message));
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return 0;
	}
}
