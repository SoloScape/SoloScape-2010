package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.player.Player;

public final class RunToggleButton extends ChildInterface implements ClickActionHandler {
	public RunToggleButton(int childId) {
		super(childId);
		// Settings uses the plain click packet; the minimap orb uses option one.
		addOption(0, this, false);
		addOption(1, this, false);
	}

	@Override
	public void handleAction(Player player, ChildInterfaceClickAction action) {
		player.setRunning(!player.isRunning());
	}
}
