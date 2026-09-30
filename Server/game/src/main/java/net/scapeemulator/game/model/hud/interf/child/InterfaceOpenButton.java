package net.scapeemulator.game.model.hud.interf.child;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.player.Player;

public class InterfaceOpenButton extends ChildInterface implements ClickActionHandler {
	private final Interface[] interf;
	private final String name;

	public InterfaceOpenButton(int childId, String name, Interface... interf) {
		super(childId);
		this.interf = interf;
		this.name = name;
		addOption(1, this, false);
	}

	@Override
	public void handleAction(Player player, ChildInterfaceClickAction action) {
		boolean force = false, failed = false;
		for (Interface inter : interf) {
			if (!player.getDisplay().open(inter, force)) {// ?
				player.sendMessage("Please close the interface you have open before opening " + name + ".");
				failed = true;
				break;
			}
			force = true;
		}
		if (failed) {
			for (Interface inter : interf) {
				player.getDisplay().close(inter);
			}
		}
	}
}