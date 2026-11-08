package net.scapeemulator.game.model.hud.util;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;

public class WorldMap extends Interface {
	public static final int CENTER_POSITION_VAR = 622;
	public static final int PLAYER_POSITION_VAR = 674;
	public static final Interface INSTANCE = new WorldMap();

	public WorldMap() {
		super(755, InterfacePersistence.TRANSIENT);
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.getStatus().setVarC(CENTER_POSITION_VAR, player.getPosition().toPackedInt());
			player.getStatus().setVarC(PLAYER_POSITION_VAR, player.getPosition().toPackedInt());
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
