package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.GlobalIntVarMessage;

public class WorldMap extends Interface {
	public static final Interface INSTANCE = new WorldMap();

	public WorldMap() {
		super(755, InterfacePersistence.TRANSIENT);
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.send(new GlobalIntVarMessage(622, player.getPosition().toPackedInt()));
			player.send(new GlobalIntVarMessage(674, player.getPosition().toPackedInt()));
			break;
		default:
			break;
		}
	}

	@Override
	public int getDisplaySlot(DisplayMode mode) {
		return DisplayMode.FULLSCREEN_INTERFACE;
	}
}
