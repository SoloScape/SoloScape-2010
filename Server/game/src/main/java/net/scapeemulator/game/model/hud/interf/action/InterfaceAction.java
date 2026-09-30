package net.scapeemulator.game.model.hud.interf.action;

import net.scapeemulator.api.message.Message;

public class InterfaceAction implements Message {
	public static final InterfaceAction OPEN_ACTION = new InterfaceAction(ActionType.OPEN_INTER),
			CLOSE_ACTION = new InterfaceAction(ActionType.CLOSE_INTER);
	private final int interfaceId;
	private final ActionType type;

	public InterfaceAction(ActionType type) {
		this.type = type;
		interfaceId = -1;
	}

	@Override
	public String toString() {
		return "InterfaceAction [interfaceId=" + interfaceId + ", type=" + type + "]";
	}

	public InterfaceAction(ActionType type, int interfaceId) {
		this.type = type;
		this.interfaceId = interfaceId;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public ActionType getType() {
		return type;
	}

	public static enum ActionType {
		OPEN_INTER, CLOSE_INTER, CHILD_ACTION;
	}
}
