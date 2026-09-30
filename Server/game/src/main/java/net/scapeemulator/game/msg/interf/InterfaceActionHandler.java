package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.player.Player;

public class InterfaceActionHandler<E extends InterfaceAction> extends MessageHandler<Player, E> {

	@Override
	public void handle(Player player, InterfaceAction action) {
		int interfaceId = action.getInterfaceId();
		if (interfaceId != -1) {
			Interface inter = player.getDisplay().getInterface(interfaceId);
			if (inter != null) {
				inter.dispatch(player, action);
			}
		} else {
			switch (action.getType()) {
			case CLOSE_INTER:
				player.getDisplay().closeInterfaces();
				break;
			default:
				break;
			}
		}
	}
}
