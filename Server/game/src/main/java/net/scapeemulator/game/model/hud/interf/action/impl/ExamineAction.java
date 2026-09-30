package net.scapeemulator.game.model.hud.interf.action.impl;

import net.scapeemulator.game.model.hud.interf.action.InventoryActionHandler;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public class ExamineAction implements InventoryActionHandler {
	public static final ExamineAction ACTION = new ExamineAction();

	@Override
	public void handle(Player player, int slot, Item item, Inventory source) {
		player.sendMessage("It's a " + item.getDefinition().getName() + ".");
	}
}
