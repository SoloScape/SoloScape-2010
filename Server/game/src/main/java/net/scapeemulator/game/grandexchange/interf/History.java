package net.scapeemulator.game.grandexchange.interf;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.PaneInterface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;

public class History extends PaneInterface {
	public static final Interface INSTANCE = new History();

	public History() {
		super(643, InterfacePersistence.TRANSIENT);
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			break;
		default:
			break;
		}
	}
}
