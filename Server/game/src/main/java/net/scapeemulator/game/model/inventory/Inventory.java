package net.scapeemulator.game.model.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.scapeemulator.game.model.player.Player;

public final class Inventory {
	public static final int PRICE_CHECKER = 90;
	public static final int BACKPACK = 93;
	public static final int EQUIPMENT = 94;
	public static final int BANK = 95;

	public static final int GENERAL_STORE_FREEBIES = 35;

	/**
	 * Random inv-inter relations: inter-inv
	 * 
	 * 335: 90 Trading
	 * 
	 * 364: 141 Treasure_Trail
	 * 
	 * 477: 482 Custom fur clothing
	 * 
	 * 197: 347 Mage training arena shop
	 * 
	 * 626: 134 Duel fight confirmation
	 * 
	 * 
	 * script 106: 140 RELATED SOUNDEFFECT 1859
	 * 
	 * 3501 script: container 207
	 * 
	 * 2116, 2120 script: 307
	 * 
	 * 1232 script: 0
	 */
	private static Map<Integer, InventoryInfo> details = new HashMap<>();

	public static void addDetails(InventoryInfo info) {
		details.put(info.getInventoryId(), info);
	}

	public static Collection<InventoryInfo> getDetails() {
		return details.values();
	}

	public static InventoryInfo getDetails(int invId) {
		InventoryInfo info = details.get(invId);
		if (info == null)
			throw new NullPointerException("Unregistered Inventory ID! " + invId);
		return info;
	}

	public enum StackMode {
		ALWAYS, STACKABLE_ONLY;
	}

	private final StackMode stackMode;
	private final Item[] items;
	private final List<InventoryListener> listeners = new ArrayList<>();
	private final Player player;
	private final boolean remove_empty;

	public Inventory(Player player, InventoryInfo info) {
		this.player = player;
		stackMode = info.getStackMode();
		remove_empty = info.clearEmpty();
		items = new Item[info.getMaxCapacity()];
		for (InventoryListener listener : info.getListeners()) {
			addListener(listener);
		}
	}

	public Inventory(Inventory inventory) {
		this.stackMode = inventory.stackMode;
		this.player = inventory.player;
		this.remove_empty = inventory.remove_empty;
		this.items = inventory.toArray();
		for (InventoryListener listener : inventory.listeners) {
			addListener(listener);
		}
	}

	private Inventory(Player player, Inventory inventory) {
		this.stackMode = inventory.stackMode;
		this.player = player;
		this.items = inventory.items;
		this.remove_empty = inventory.remove_empty;
		for (InventoryListener listener : inventory.listeners) {
			addListener(listener);
		}
	}

	/**
	 * Provides a shale copy of this inventory for the given player. Shale
	 * copies are used to share an inventory between two or more players.
	 * 
	 * @param player
	 *            the player to provide this copy to.
	 * @return the copied interface.
	 */
	public Inventory shaleCopy(Player player) {
		return new Inventory(player, this);
	}

	public Item[] toArray() {
		Item[] array = new Item[items.length];
		System.arraycopy(items, 0, array, 0, items.length);
		return array;
	}

	public void addListener(InventoryListener listener) {
		listeners.add(listener);
	}

	public void removeListener(InventoryListener listener) {
		listeners.remove(listener);
	}

	public void removeListeners() {
		listeners.clear();
	}

	public void refresh() {
		fireItemsChanged();
	}

	public void refresh(int slot) {
		checkSlot(slot);
		fireItemChanged(slot);
	}

	public Item get(int slot) {
		checkSlot(slot);
		return items[slot];
	}

	public void set(int slot, Item item) {
		checkSlot(slot);
		items[slot] = item;
		fireItemChanged(slot);
	}

	public void swap(int slot1, int slot2) {
		checkSlot(slot1);
		checkSlot(slot2);

		Item tmp = items[slot1];
		items[slot1] = items[slot2];
		items[slot2] = tmp;

		fireItemChanged(slot1);
		fireItemChanged(slot2);
	}

	public void insert(int slot, int targetSlot) {
		Item temp = items[slot];
		if (targetSlot > slot) {
			for (int i = slot; i < targetSlot; i++) {
				items[i] = items[i + 1];
			}
		} else if (slot > targetSlot) {
			for (int i = slot; i > targetSlot; i--) {
				items[i] = items[i - 1];
			}
		}
		items[targetSlot] = temp;
		fireItemsChanged();
	}

	public void reset(int slot) {
		set(slot, null);
	}

	public int getItemCount(Item item) {
		int itemId = item.getId();
		int count = 0;
		for (int i = 0; i < items.length; i++) {
			if ((item = items[i]) != null && item.getId() == itemId) {
				count += item.getAmount();
			}
		}
		return count;
	}

	public void remove(int slot) {
		checkSlot(slot);
		if (remove_empty) {
			items[slot] = null;
		} else {
			items[slot] = new Item(items[slot].getId(), 0);
		}
		fireItemChanged(slot);
	}

	public Item add(Item item) {
		return add(item, -1);
	}

	public Item add(Item item, int preferredSlot) {
		int id = item.getId();
		boolean stackable = isStackable(item);
		if (stackable) {
			/* try to add this item to an existing stack */
			int slot = slotOf(id);
			if (slot != -1) {
				Item other = items[slot];
				long total = (long) other.getAmount() + item.getAmount();
				int amount;

				/* check if there are too many items in the stack */
				Item remaining = null;
				if (total > Integer.MAX_VALUE) {
					amount = Integer.MAX_VALUE;
					remaining = new Item(id, (int) (total - amount));
					fireCapacityExceeded();
				} else {
					amount = (int) total;
				}

				/* update stack and return any remaining items */
				set(slot, new Item(item.getId(), amount));
				return remaining;
			}

			/* try to add this item to the preferred slot */
			if (preferredSlot != -1) {
				checkSlot(preferredSlot);
				if (items[preferredSlot] == null) {
					set(preferredSlot, item);
					return null;
				}
			}

			/* try to add this item to any slot */
			for (slot = 0; slot < items.length; slot++) {
				if (items[slot] == null) {
					set(slot, item);
					return null;
				}
			}

			/* give up */
			fireCapacityExceeded();
			return item;
		} else {
			final Item single = new Item(id, 1);
			int remaining = item.getAmount();

			if (remaining == 0)
				return null;

			/* try to first place item at the preferred slot */
			if (preferredSlot != -1) {
				checkSlot(preferredSlot);
				if (items[preferredSlot] == null) {
					set(preferredSlot, single);
					remaining--;
				}
			}

			if (remaining == 0)
				return null;

			/*
			 * place any subsequent remaining items wherever space is available
			 */
			for (int slot = 0; slot < items.length; slot++) {
				if (items[slot] == null) {
					set(slot, single);
					remaining--;
				}

				if (remaining == 0)
					return null;
			}

			/* give up */
			fireCapacityExceeded();
			return new Item(id, remaining);
		}
	}

	public int freeSlot() {
		for (int slot = 0; slot < items.length; slot++) {
			if (items[slot] == null) {
				return slot;
			}
		}
		return -1;
	}

	public Item remove(Item item) {
		return remove(item, -1);
	}

	public Item remove(Item item, int preferredSlot) {
		int id = item.getId();
		boolean stackable = isStackable(item);

		if (stackable) {
			/* try to remove this item from its stack */
			int slot = slotOf(id);
			if (slot != -1) {
				Item other = items[slot];
				if (other.getAmount() <= item.getAmount()) {
					remove(slot);
					return new Item(id, other.getAmount());
				} else {
					other = new Item(id, other.getAmount() - item.getAmount());
					set(slot, other);
					return item;
				}
			}

			return null;
		} else {
			int removed = 0;

			/* try to remove the item from the preferred slot first */
			if (preferredSlot != -1) {
				checkSlot(preferredSlot);
				if (items[preferredSlot].getId() == id) {
					set(preferredSlot, null);

					if (++removed >= item.getAmount())
						return new Item(id, removed);
				}
			}

			/* try other slots */
			for (int slot = 0; slot < items.length; slot++) {
				Item other = items[slot];
				if (other != null && other.getId() == id) {
					set(slot, null);

					if (++removed >= item.getAmount())
						return new Item(id, removed);
				}
			}

			return removed == 0 ? null : new Item(id, removed);
		}
	}

	public void shift() {
		int destSlot = 0;

		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item != null) {
				items[destSlot++] = item;
			}
		}

		for (int slot = destSlot; slot < items.length; slot++)
			items[slot] = null;

		fireItemsChanged();
	}

	public void empty() {
		for (int slot = 0; slot < items.length; slot++)
			items[slot] = null;

		fireItemsChanged();
	}

	public boolean isEmpty() {
		for (int slot = 0; slot < items.length; slot++)
			if (items[slot] != null)
				return false;

		return true;
	}

	public int freeSlots() {
		int slots = 0;
		for (int slot = 0; slot < items.length; slot++)
			if (items[slot] == null)
				slots++;

		return slots;
	}

	public int slotOf(int id) {
		for (int slot = 0; slot < items.length; slot++) {
			Item item = items[slot];
			if (item != null && item.getId() == id)
				return slot;
		}

		return -1;
	}

	public boolean contains(int id) {
		return slotOf(id) != -1;
	}

	private void fireItemChanged(int slot) {
		for (InventoryListener listener : listeners)
			listener.itemChanged(player, this, slot, items[slot]);
	}

	private void fireItemsChanged() {
		for (InventoryListener listener : listeners)
			listener.itemsChanged(player, this);
	}

	public void fireCapacityExceeded() {
		for (InventoryListener listener : listeners)
			listener.capacityExceeded(player, this);
	}

	private boolean isStackable(Item item) {
		if (stackMode == StackMode.ALWAYS)
			return true;

		return item.getDefinition().isStackable();
	}

	public int size() {
		return items.length;
	}

	private void checkSlot(int slot) {
		if (slot < 0 || slot >= items.length)
			throw new IndexOutOfBoundsException("slot out of range");
	}
}
