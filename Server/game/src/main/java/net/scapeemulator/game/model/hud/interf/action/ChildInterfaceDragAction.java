package net.scapeemulator.game.model.hud.interf.action;

import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceActionType;

public class ChildInterfaceDragAction extends ChildInterfaceAction {
	private final int targetId, targetChild;
	private final int slot, targetSlot, parameter, targetParameter;

	public ChildInterfaceDragAction(int interfaceId, int child, int slot, int parameter, int targetId, int targetChild,
			int targetSlot, int targetParameter) {
		super(ChildInterfaceActionType.DRAG, interfaceId, child);
		this.targetId = targetId;
		this.targetChild = targetChild;

		this.slot = slot;
		this.targetSlot = targetSlot;
		this.parameter = parameter;
		this.targetParameter = targetParameter;
	}

	public int getTargetId() {
		return targetId;
	}

	public int getTargetChild() {
		return targetChild;
	}

	public int getSlot() {
		return slot;
	}

	public int getTargetSlot() {
		return targetSlot;
	}

	public int getParameter() {
		return parameter;
	}

	public int getTargetParameter() {
		return targetParameter;
	}
}
