package net.scapeemulator.game.model.shop;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.cache.def.GameConstantContainer;
import net.scapeemulator.game.cache.GameConstants;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public class Shop {
	private final ShopConfiguration configuration;
	private final Inventory inventory;
	@SuppressWarnings("unchecked")
	private final static GameConstantContainer<Integer> tokkulValues = (GameConstantContainer<Integer>) GameConstants
			.getConstants(731), staticValues = (GameConstantContainer<Integer>) GameConstants.getConstants(733);

	public Shop(ShopConfiguration configuration) {
		this.configuration = configuration;
		this.inventory = new Inventory(configuration.getInventory());
	}

	public void buy(Player player, int slot, int amount) {
		Item item = inventory.get(slot);
		if (item != null) {
			Inventory pInv = player.getInventories().get(Inventory.BACKPACK);
			int currencySlot = pInv.slotOf(configuration.getCurrency());
			Item currency = currencySlot == -1 ? null : pInv.get(currencySlot);

			int totalValue = 0, freeSlots;
			if (currency == null && amount != 0) {
				player.sendMessage("You don't have enough coins.");
				return;
			} else {
				if (amount == 0) {
					int defaultStock = getClientFormatStock(slot), currentStock = inventory.get(slot).getAmount();
					// value
					if (defaultStock == -1) {
						player.sendMessage("The shop has run out of stock.");
						return;
					}
					int value = getBuyPrice(slot, currentStock, defaultStock);
					player.sendMessage(item.getDefinition().getName() + ": shop will sell for " + value + " "
							+ currencyName() + ".", 0);
				} else {
					if (item.getDefinition().isStackable()) {
						freeSlots = Integer.MAX_VALUE - item.getAmount();
					} else {
						freeSlots = pInv.freeSlots();
					}
					int defaultStock = getClientFormatStock(slot), currentStock = inventory.get(slot).getAmount();
					int add = 0;
					if (defaultStock == -1) {
						player.sendMessage("The shop has run out of stock.");
						return;
					}
					for (; add < amount;) {
						if (currentStock == 0) {
							player.sendMessage("The shop has run out of stock.");
							break;
						}
						int value = getBuyPrice(slot, currentStock, defaultStock);
						if (freeSlots == 0) {
							// Ran out of inventory space
							if (currency.getAmount() == (long) value + totalValue) {
								freeSlots++;
							} else {
								pInv.fireCapacityExceeded();
								break;
							}
						}
						if (currency.getAmount() < (long) value + totalValue) {
							player.sendMessage("You don't have enough coins.");
							break;
						}
						totalValue += value;
						currentStock--;
						add++;
						freeSlots--;
					}
					if (add != 0) {
						pInv.remove(new Item(configuration.getCurrency(), totalValue));
						Inventory shop = player.getInventories().get(getId());

						Item result = new Item(item.getId(), add);
						shop.remove(result);
						pInv.add(result);
						player.sendMessage("Buy item " + inventory.get(slot).getDefinition().getName() + ": " + add
								+ " (requested " + amount + ")");
					}
				}
			}
		}
	}

	public void sell(Player player, int slot, int amount) {
		Inventory pInv = player.getInventories().get(Inventory.BACKPACK);
		Item item = null;
		// TODO: Check if selling stuff exceeds max cash amount?

		if (pInv != null && (item = pInv.get(slot)) != null) {
			// Check if item is possible to sell into a shop
			int definitionValue = item.getDefinition().getValue();
			if (definitionValue == 0) {
				player.sendMessage("You can't sell this item.", 0);
			}
			boolean generalStore = inventory.size() == 40;

			int shopItemId = item.getId();
			if (item.getDefinition().getNoteTemplate() != -1) {
				shopItemId = item.getDefinition().getNoteBase();
			}
			
			int shopSlot = configuration.getInventory().slotOf(shopItemId);
			if (!generalStore && shopSlot == -1) {
				player.sendMessage("You can't sell this item to this shop.", 0);
				return;
			}
			
			int stockAmount = 0, baseMultiplier = 60, hike = 2, priceModMax = 8;
			if (shopSlot == -1) {
				baseMultiplier = 40;
				priceModMax = 10;
				hike = 3;
				shopSlot = inventory.slotOf(shopItemId);
				if (shopSlot == -1) {
					shopSlot = inventory.freeSlot();
				}
			} else {
				stockAmount = configuration.getInventory().get(shopSlot).getAmount();// ??
			}
			
			int shopAmount = 0;
			if (shopSlot != -1 && inventory.get(shopSlot) != null) {
				shopAmount = inventory.get(shopSlot).getAmount() - stockAmount;
			}
			
			boolean noCalculation = false;
			if (baseMultiplier == 60) {
				if (shopAmount < -4) {
					shopAmount = -4;
				}
			}
			if (shopAmount > priceModMax) {
				shopAmount = priceModMax;
				noCalculation = true;
			}

			double factor = ((double) (baseMultiplier - (hike * shopAmount))) / 100;
			if (amount == 0) {
				int value = (int) Math.floor(factor * definitionValue);
				player.sendMessage(
						item.getDefinition().getName() + ": shop will buy for " + value + " " + currencyName() + ".",
						0);
			} else {
				if (shopSlot == -1) {
					player.sendMessage("This shop is full.", 0);// ???
					return;
				}
				// TODO: Fits
				Inventory shopInv = player.getInventories().get(getId());
				int fits = Integer.MAX_VALUE - amount;
				if (fits == 0) {
					shopInv.fireCapacityExceeded();
					return;
				}
				if (fits < amount) {
					amount = fits;
					shopInv.fireCapacityExceeded();
				}
				if (amount == 1) {
					noCalculation = true;
				} else {
					int invCount = pInv.getItemCount(item);
					if (invCount < amount)
						amount = invCount;
				}
				// Correct
				int yield = 0;
				int remaining = amount;
				if (!noCalculation) {
					for (int i = 0; i < amount; i++) {
						shopAmount++;
						if (shopAmount > priceModMax) {
							shopAmount = priceModMax;
							break;
						}
						remaining--;
						factor = ((double) (baseMultiplier - (hike * shopAmount))) / 100;
						yield += (int) Math.floor(definitionValue * factor);
					}
				}
				yield += (int) (Math.floor(definitionValue * factor) * remaining);
				yield = Math.max(0, yield);
				pInv.remove(new Item(item.getId(), amount));
				if (yield != 0)
					pInv.add(new Item(configuration.getCurrency(), yield));

				shopInv.add(new Item(shopItemId, amount));
				// value
			}
		}
	}

	public int getClientFormatStock(int slot) {
		if (configuration.getInventory().get(slot) != null) {
			int amount = inventory.get(slot).getAmount();
			return amount == 0 ? -1 : configuration.getInventory().get(slot).getAmount();
		} else {
			return 0;
		}
	}

	public int getStaticValue(int itemId) {
		int value = -1;
		if (configuration.getCurrency() == 6529) {
			value = tokkulValues.getValue(itemId);
			if (value != -1 && value > 0)
				return value;
		}
		value = staticValues.getValue(itemId);
		if (value != -1 && value > 0)
			return value;
		return -1;
	}

	public int getBuyPrice(int slot) {
		return getBuyPrice(slot, inventory.get(slot).getAmount(), getClientFormatStock(slot));
	}

	public int getBuyPrice(int slot, int currentStock, int defaultStock) {
		Item item = inventory.get(slot);
		// if (((boolean) getItemHashmapData(itemId, 258)) || ((boolean)
		// getItemHashmapData(itemId, 259))) {
		// return 99000;
		// }
		if (item == null || item.getAmount() == 0) {
			return 0;
		}
		// TODO: Mastery :|
		int value = getStaticValue(item.getId());
		if (defaultStock == -1) {
			return -1;
		}
		int factor = 0;
		if (defaultStock == 1) {
			factor = 100;
		} else if (currentStock == 1) {
			factor = 130;
		} else if (currentStock >= defaultStock) {
			factor = 100;
		} else {
			factor = 100 + (30 - (30 * currentStock / defaultStock));
		}
		factor = Math.max(100, Math.min(130, factor));

		value = (item.getDefinition().getValue() * factor) / 100;
		if (configuration.getCurrency() == 6529) {
			value = value / 2 * 3;
		}
		return Math.max(value, 1);
	}

	private String currencyName() {
		return "coins";
	}

	public ShopConfiguration getConfiguration() {
		return configuration;
	}

	public int getId() {
		return configuration.getShopId();
	}

	public Inventory getInventory() {
		return inventory;
	}

	private static final Map<Integer, Shop> shops = new HashMap<>();

	static {
		register(new Shop(new ShopConfiguration(0, "Ayy weapons lmao", -1)));
		register(new Shop(new ShopConfiguration(3, "General Store", 35)));
	}

	public static void register(Shop shop) {
		shops.put(shop.getId(), shop);
	}

	public static Shop getShop(int shopId) {
		return shops.get(shopId);
	}
}
