package net.scapeemulator.game.model.hud.interf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceAction;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceDragAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.interf.handler.EntryOnEntryHandler;
import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceType;
import net.scapeemulator.game.model.hud.interf.type.DragAllowance;
import net.scapeemulator.game.model.hud.interf.type.EntryOnEntryType;
import net.scapeemulator.game.model.hud.interf.type.InternalDragAllowance;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.child.ChildInterfaceSettings;

public abstract class ChildInterface {
	private final Map<Integer, ClickActionHandler> options = new HashMap<>();
	private final List<Integer> externalOptions = new ArrayList<>();
	private final List<ChildInterfaceSettings> settings = new ArrayList<>();
	private final int childId;

	public ChildInterface(int childId) {
		this.childId = childId;
	}

	public void addOption(int optId, ClickActionHandler handler, boolean external) {
		options.put(optId, handler);
		if (external)
			externalOptions.add(optId);
	}

	protected void setSettings(int interfaceId) {
		ChildInterfaceSettings primary = new ChildInterfaceSettings(interfaceId, childId);
		primary.setAffectingSlots(firstSlot(), size() - 1);
		primary.enableOptions(getEnabledOptions());
		Map<EntryOnEntryType, EntryOnEntryHandler> handlers = getHandlers();
		if (handlers != null)
			primary.setEntryOnEntrySettings(handlers.keySet().toArray(new EntryOnEntryType[0]));
		primary.setDepth(getDragAllowance().ordinal());
		ChildInterfaceSettings secondary = null;
		ChildInterfaceSettings endlist = null;
		switch (getInternalDragAllowance()) {
		case ENDLIST_DRAG:
			endlist = new ChildInterfaceSettings(interfaceId, childId);
			endlist.setAffectingSlots(size() * 2, size() * 2);
			endlist.setInternalInputEnabled(true);
		case EMPTY_ENTRIES:
			secondary = new ChildInterfaceSettings(interfaceId, childId);
			secondary.setAffectingSlots(size() + firstSlot(), (size() * 2) - 1);
			secondary.enableOptions(externalOptions.toArray(new Integer[0]));
			secondary.setInternalInputEnabled(true);
		case EXISTING_ENTRIES:
			primary.setInternalInputEnabled(true);
			break;
		case NONE:
			break;
		}
		settings.add(primary);
		if (secondary != null)
			settings.add(secondary);
		if (endlist != null)
			settings.add(endlist);
		System.out.println(primary + " " + secondary + " " + endlist);
	}

	protected Integer[] getEnabledOptions() {
		return options.keySet().toArray(new Integer[0]);
	}

	public void handle(Player player, ChildInterfaceAction childAction) {
		switch (childAction.getSubtype()) {
		case BUTTON:
			ChildInterfaceClickAction clickAction = (ChildInterfaceClickAction) childAction;
			if (options.containsKey(clickAction.getOption())) {
				options.get(clickAction.getOption()).handleAction(player, clickAction);
			} else {
				player.sendMessage("Unhandled child click action: " + childAction, 99);
			}
			break;
		case DRAG:
			ChildInterfaceDragAction dragAction = (ChildInterfaceDragAction) childAction;
			if (dragAction.getInterfaceId() != dragAction.getTargetId()) {
				if (getDragAllowance().ordinal() >= DragAllowance.EXTERNAL.ordinal())
					externalDrag(player, dragAction);
			} else {
				internalDrag(player, dragAction);
			}
			break;
		default:
			break;
		}
	}

	protected void internalDrag(Player player, ChildInterfaceDragAction dragAction) {

	}

	protected void externalDrag(Player player, ChildInterfaceDragAction dragAction) {

	}

	public Object[] settings() {
		return settings.toArray();
	}

	public final EntryOnEntryHandler getHandler(EntryOnEntryType type) {
		Map<EntryOnEntryType, EntryOnEntryHandler> handlers = getHandlers();
		if (handlers == null)
			return null;
		return handlers.get(type);
	}

	public Map<EntryOnEntryType, EntryOnEntryHandler> getHandlers() {
		return null;
	}

	public DragAllowance getDragAllowance() {
		return DragAllowance.DENIED;
	}

	public InternalDragAllowance getInternalDragAllowance() {
		return InternalDragAllowance.NONE;
	}

	public int firstSlot() {// Wooo
		return 0;
	}

	public int size() {// Wooo
		return 0;
	}

	public int getChildId() {
		return childId;
	}

	public ChildInterfaceType getType() {
		return ChildInterfaceType.CHILD_INTERFACE;
	}
}