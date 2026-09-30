package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.impl.CloseAction;

public final class CloseChild extends ChildInterface {

	public CloseChild(int childId, Interface inter) {
		super(childId);
		addOption(1, new CloseAction(inter), false);
	}
}
