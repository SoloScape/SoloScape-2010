package net.scapeemulator.game.model.hud.interf.action;

import net.scapeemulator.game.model.player.Player;

public interface ClickActionHandler {
	public abstract void handleAction(Player player, ChildInterfaceClickAction action);
}
