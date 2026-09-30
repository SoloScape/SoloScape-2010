package net.scapeemulator.game.model.hud.interf.action;

import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceActionType;

public final class ChildInterfaceClickAction extends ChildInterfaceAction {
	private final int option, parameter, slot;

	public ChildInterfaceClickAction(int option, int id, int child, int slot, int parameter) {
		super(ChildInterfaceActionType.BUTTON, id, child);
		this.option = option;
		this.slot = slot;
		this.parameter = parameter;
	}

	public int getOption() {
		return option;
	}

	public int getSlot() {
		return slot;
	}

	@Override
	public String toString() {
		return "ChildInterfaceClickAction [getInterfaceId()=" + getInterfaceId() + ", getChild()=" + getChild()
				+ ", slot=" + slot + ", parameter=" + parameter + ", option=" + option + "]";
	}

	public int getParameter() {
		return parameter;
	}

}
