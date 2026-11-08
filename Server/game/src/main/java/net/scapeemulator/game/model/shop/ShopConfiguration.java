package net.scapeemulator.game.model.shop;

import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Inventory.StackMode;
import net.scapeemulator.game.model.inventory.InventoryInfo;
import net.scapeemulator.game.model.inventory.Item;

public class ShopConfiguration {
	private final Inventory inventory;
	private final String shopName;
	private final int shopId;
	private final InventoryInfo details;
	private final int currency;
	private final int freebieInventoryId;

	public ShopConfiguration(int shopId, String shopName, int freebieInventoryId) {
		this.freebieInventoryId = freebieInventoryId;
		details = new InventoryInfo(shopId, "", 620, 24, 40, StackMode.ALWAYS);
		details.disableClearEmpty();
		currency = 995;
		inventory = new Inventory(null, details);
		inventory.addListener(new ShopStockChecker(inventory));
		testItems();
		this.shopId = shopId;
		this.shopName = shopName;
	}

	private void testItems() {// Main stock
		inventory.add(new Item(4151, 100));
		inventory.add(new Item(4153, 100));
		inventory.add(new Item(13736, 100));
		inventory.add(new Item(13738, 100));
		inventory.add(new Item(13740, 100));
		inventory.add(new Item(13742, 100));
		inventory.add(new Item(13744, 100));
		inventory.add(new Item(14484, 100));
		inventory.add(new Item(11694, 100));
		inventory.add(new Item(11696, 100));
		inventory.add(new Item(11698, 100));
		inventory.add(new Item(11700, 100));
		inventory.add(new Item(15259, 100));
		inventory.add(new Item(11335, 100));
		inventory.add(new Item(3140, 100));
		inventory.add(new Item(14479, 100));
	}

	public int getShopId() {
		return shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public InventoryInfo getDetails() {
		return details;
	}

	public int getCurrency() {
		return currency;
	}

	public int getFreebieInventoryId() {
		return freebieInventoryId;
	}
}
