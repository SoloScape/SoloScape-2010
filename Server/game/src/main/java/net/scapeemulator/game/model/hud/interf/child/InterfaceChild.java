package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.ChildInterfaceType;

public class InterfaceChild extends ChildInterface {
	private final Interface inter;

	public InterfaceChild(int childId, Interface inter) {
		super(childId);
		this.inter = inter;
	}

	@Override
	public ChildInterfaceType getType() {
		return ChildInterfaceType.INTERFACE;
	}

	public Interface getInter() {
		return inter;
	}
}
