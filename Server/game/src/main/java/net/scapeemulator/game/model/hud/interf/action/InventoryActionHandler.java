package net.scapeemulator.game.model.hud.interf.action;

import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public interface InventoryActionHandler {
	public abstract void handle(Player player, int slot, Item item, Inventory source);
}
