package net.scapeemulator.game.model.hud.interf.action.impl;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.player.Player;

public class CloseAction implements ClickActionHandler {
	private final Interface inter;

	public CloseAction(Interface inter) {
		this.inter = inter;
	}

	@Override
	public void handleAction(Player player, ChildInterfaceClickAction action) {
		player.getDisplay().close(inter);
	}
}
