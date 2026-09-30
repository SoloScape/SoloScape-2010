package net.scapeemulator.game.model.inventory;

import java.util.ArrayList;
import java.util.List;

import net.scapeemulator.game.model.inventory.Inventory.StackMode;

public class InventoryInfo {
	private final StackMode stackType;
	private final int inventoryId, interId, interChild, maxCapacity;
	private final String name;
	private final List<InventoryListener> listeners;
	private boolean clearEmpty = true;

	public InventoryInfo(int inventoryId, String name, int interId, int interChild, int maxCapacity,
			StackMode stackType, InventoryListener... others) {
		this.inventoryId = inventoryId;
		this.name = name;
		this.interId = interId;
		this.interChild = interChild;
		this.maxCapacity = maxCapacity;
		this.stackType = stackType;
		listeners = new ArrayList<>();
		listeners.add(new InventoryFullListener(name));
		listeners.add(new InventoryMessageListener(interId, interChild, inventoryId));
		for (InventoryListener listener : others)
			listeners.add(listener);
	}

	public void disableClearEmpty() {
		clearEmpty = false;
	}

	public StackMode getStackMode() {
		return stackType;
	}

	public int getInventoryId() {
		return inventoryId;
	}

	public int getInterId() {
		return interId;
	}

	public int getInterChild() {
		return interChild;
	}

	public String getName() {
		return name;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public List<InventoryListener> getListeners() {
		return listeners;
	}

	public boolean clearEmpty() {
		return clearEmpty;
	}
}
