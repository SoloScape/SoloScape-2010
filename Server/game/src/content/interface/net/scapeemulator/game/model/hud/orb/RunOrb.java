package net.scapeemulator.game.model.hud.orb;

import net.scapeemulator.game.model.hud.interf.child.RunToggleButton;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.player.Player;

public class RunOrb extends Orb {
	public static final RunOrb INSTANCE = new RunOrb();

	public RunOrb() {
		super(2, 750);
		RunToggleButton button = new RunToggleButton(1);
		button.addOption(2, new ClickActionHandler() {
			@Override
			public void handleAction(Player player, ChildInterfaceClickAction action) {
				player.startResting();
			}
		}, false);
		addChild(button);
	}
}
