package net.scapeemulator.game.model.hud.interf.child;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceAction;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceDragAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.interf.action.InventoryActionHandler;
import net.scapeemulator.game.model.hud.interf.handler.EntryOnEntryHandler;
import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceType;
import net.scapeemulator.game.model.hud.interf.type.DragAllowance;
import net.scapeemulator.game.model.hud.interf.type.EntryOnEntryType;
import net.scapeemulator.game.model.hud.interf.type.InternalDragAllowance;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.InventoryInfo;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Player;

public abstract class InventoryChild extends ChildInterface {
	private final Map<EntryOnEntryType, EntryOnEntryHandler> handlers = new HashMap<>();
	private final Map<Integer, InventoryActionHandler> inventoryActions = new HashMap<>();
	protected final InventoryInfo invDetails;

	public InventoryChild(int childId, InventoryInfo invDetails) {
		super(childId);
		this.invDetails = invDetails;
	}

	@Override
	public Map<EntryOnEntryType, EntryOnEntryHandler> getHandlers() {
		return handlers;
	}

	@Override
	public final void addOption(int optId, ClickActionHandler handler, boolean external) {
		throw new IllegalAccessError(
				"Can't add a ClickActionHandler for an inventory type child! Use addAction(optId, handler) instead!");
	}

	public void addAction(int optionId, InventoryActionHandler handler) {
		inventoryActions.put(optionId, handler);
	}

	@Override
	protected Integer[] getEnabledOptions() {
		return inventoryActions.keySet().toArray(new Integer[0]);
	}

	@Override
	public DragAllowance getDragAllowance() {
		return DragAllowance.CHILD_INTERNAL;
	}

	@Override
	public void handle(Player player, ChildInterfaceAction childAction) {
		Inventory inventory = player.getInventories().get(invDetails.getInventoryId());
		switch (childAction.getSubtype()) {
		case BUTTON:
			ChildInterfaceClickAction click = (ChildInterfaceClickAction) childAction;
			int slot = click.getSlot();
			int itemId = click.getParameter();
			int option = click.getOption();
			if (slot < 0 || slot >= size()) {// Cheating
				return;
			} else {
				Item item = inventory.get(slot);
				if (item == null || itemId != item.getId()) {
					inventory.refresh(slot);
					return;
				}
				InventoryActionHandler handler = inventoryActions.get(option);
				if (handler != null) {
					handler.handle(player, slot, item, inventory);
				}
			}
			break;
		case DRAG:
			ChildInterfaceDragAction drag = (ChildInterfaceDragAction) childAction;
			if (drag.getInterfaceId() != drag.getTargetId()) {
				if (getDragAllowance().ordinal() < DragAllowance.EXTERNAL.ordinal()) {
					return;
				}
				// TODO: External Drag event, drag from child to child
			} else {
				if (drag.getChild() != drag.getTargetChild()) {
					if (getDragAllowance().ordinal() < DragAllowance.INTERFACE_INTERNAL.ordinal()) {
						return;
					}
				} else {
					slot = drag.getSlot();
					int targetSlot = drag.getTargetSlot();
					if (targetSlot >= size()) {
						targetSlot -= size();
					}
					if (slot < 0 || slot >= size() || targetSlot < 0 || targetSlot >= size()) {
						return;
					}
					if (inventory.get(slot) == null) {
						inventory.refresh(slot);
						inventory.refresh(targetSlot);
						return;
					}
					if (!insert(player)) {
						inventory.swap(slot, targetSlot);
					} else {
						inventory.insert(slot, targetSlot);
					}
				}
			}
			break;
		default:
			break;
		}
	}

	protected boolean insert(Player player) {
		return false;
	}

	@Override
	public ChildInterfaceType getType() {
		return ChildInterfaceType.INVENTORY;
	}

	@Override
	public InternalDragAllowance getInternalDragAllowance() {
		// Override if other allowance.
		return InternalDragAllowance.EMPTY_ENTRIES;
	}

	@Override
	public int size() {
		return invDetails.getMaxCapacity();
	}

	public int getAssignedId() {
		return invDetails.getInventoryId();
	}
}
