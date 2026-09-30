package net.scapeemulator.game.model.hud.interf.action;

import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceActionType;

public abstract class ChildInterfaceAction extends InterfaceAction {
	private final int child;
	private final ChildInterfaceActionType subtype;

	public ChildInterfaceAction(ChildInterfaceActionType subtype, int interfaceId, int child) {
		super(ActionType.CHILD_ACTION, interfaceId);
		this.subtype = subtype;
		this.child = child;
	}

	public int getChild() {
		return child;
	}

	public ChildInterfaceActionType getSubtype() {
		return subtype;
	}
}
