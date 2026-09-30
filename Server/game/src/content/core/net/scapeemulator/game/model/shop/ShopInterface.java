package net.scapeemulator.game.model.shop;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public class ShopInterface extends Interface {
	private final Shop shop;
	private final int freebieId;

	public ShopInterface(Shop shop) {
		super(620, InterfacePersistence.TRANSIENT);
		this.shop = shop;
		addChild(new ShopInventory(25, 118, shop));// Main
		freebieId = shop.getConfiguration().getFreebieInventoryId();
		if (freebieId != -1) {
			addChild(new ShopInventory(26, 1496, shop));// Freebie
		}
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.getInventories().addInventory(shop.getId(), shop.getInventory().shaleCopy(player));
			player.getInventories().get(shop.getId()).refresh();
			if (freebieId != -1)
				player.getInventories().get(freebieId).refresh();
			player.getStatus().setVarC(743, shop.getConfiguration().getCurrency());
			player.getStatus().setVar(532, shop.getConfiguration().getCurrency());
			for (int slot = 0; slot < shop.getInventory().size(); slot++) {
				player.getStatus().setVarC(946 + slot, shop.getClientFormatStock(slot));
			}
			break;
		case CLOSE_INTER:
			player.getInventories().removeInventory(shop.getId());
			player.getStatus().setVar(118, -1);
			player.getStatus().setVar(1496, -1);
			player.getStatus().setVar(532, -1);
			// Close other inventories
			break;
		default:
			break;
		}
	}

	public static class ShopInventory extends ChildInterface {
		private final int capacity, varId;

		public ShopInventory(int childId, int varId, Shop shop) {
			super(childId);
			this.varId = varId;
			if (varId == 118) {
				capacity = shop.getInventory().size();
			} else {
				this.capacity = Inventory.getDetails(shop.getConfiguration().getFreebieInventoryId()) == null ? 0
						: Inventory.getDetails(shop.getConfiguration().getFreebieInventoryId()).getMaxCapacity();
				shop = null;
			}
			addOption(1, new InfoAction(varId, shop), false);
			addOption(2, new BuyAction(1, varId, shop), false);
			addOption(3, new BuyAction(5, varId, shop), false);
			addOption(4, new BuyAction(10, varId, shop), false);
			addOption(5, new BuyAction(50, varId, shop), false);
			if (varId == 118)
				addOption(6, new BuyAction(500, 118, shop), false);
			addOption(10, new ExamineAction(varId), false);
			setSettings(620);
		}

		public int size() {
			return capacity * (varId == 118 ? 6 : 4);
		}
	}

	public static class BuyAction implements ClickActionHandler {
		private final Shop shop;
		private final int invVarId, amount;

		public BuyAction(int amount, int invVarId, Shop shop) {
			this.invVarId = invVarId;
			this.amount = amount;
			this.shop = shop;
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			int itemSlot = action.getSlot() / (invVarId == 118 ? 6 : 4);
			int shopInv = player.getStatus().getWaitingVar(invVarId);
			Inventory inventory = player.getInventories().get(shopInv);
			if (inventory != null) {
				Item item = inventory.get(itemSlot);
				if (item != null) {
					if (shop != null) {
						shop.buy(player, itemSlot, amount);
					} else {
						//TODO: Static method Shop.claim()
						if (item.getAmount() == 0) {
							// no freebies remaining;
							return;
						}
						Inventory pInv = player.getInventories().get(Inventory.BACKPACK);
						int amount = this.amount;
						if (amount > item.getAmount()) {
							amount = item.getAmount();
						}
						Item result = pInv.add(new Item(item.getId(), amount));
						int remove = result == null ? 0 : result.getAmount();
						result = new Item(item.getId(), amount - remove);
						inventory.remove(result);
						if (inventory.get(itemSlot).getAmount() == 0) {
							// ?? Message that you have claimed all free items?
						}
						player.sendMessage("Claim item(" + item + ") from Inventory #" + shopInv + ": " + amount);
					}
				}
			}
		}
	}

	public static class InfoAction implements ClickActionHandler {
		private final Shop shop;
		private final int invVarId;

		public InfoAction(int invVarId, Shop shop) {
			this.invVarId = invVarId;
			this.shop = shop;
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			int itemSlot = action.getSlot() / (invVarId == 118 ? 6 : 4);
			int shopInv = player.getStatus().getWaitingVar(invVarId);
			Inventory inventory = player.getInventories().get(shopInv);
			if (inventory != null) {
				Item item = inventory.get(itemSlot);
				if (item != null) {
					if (shop != null) {
						shop.buy(player, itemSlot, 0);
						int price = shop.getBuyPrice(itemSlot);
						if (price == -1)
							price = 0;
						player.getStatus().setVarC(743, shop.getConfiguration().getCurrency());
						player.getStatus().setVarC(744, price);
						player.getStatus().setVarC(741, item.getId());
					} else {
						player.getStatus().setVarC(743, 0);
						player.getStatus().setVarC(744, item.getAmount() == 0 ? 0 : -1);
						player.getStatus().setVarC(741, item.getId());
						// Freebie
					}
				}
			}
		}
	}

	public static class ExamineAction implements ClickActionHandler {
		private final int invVarId;

		public ExamineAction(int invVarId) {
			this.invVarId = invVarId;
		}

		@Override
		public void handleAction(Player player, ChildInterfaceClickAction action) {
			player.sendMessage(invVarId + "", 0);
			int itemSlot = action.getSlot() / (invVarId == 118 ? 6 : 4);
			int shopInv = player.getStatus().getWaitingVar(invVarId);
			Inventory inventory = player.getInventories().get(shopInv);
			if (inventory != null) {
				Item item = inventory.get(itemSlot);
				if (item != null) {
					// Examine
				}
			}
		}
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return mode.getCenterRoot();
	}
}
