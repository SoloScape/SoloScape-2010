package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.hud.util.WorldMap;
import net.scapeemulator.game.model.player.Player;

public final class WorldMapClickMessageHandler extends MessageHandler<Player, WorldMapClickMessage> {
	@Override
	public void handle(Player player, WorldMapClickMessage message) {
		if (!message.isValid() || player.getDisplay().getInterface(WorldMap.INSTANCE.getId()) == null)
			return;
		// Recenter the map; varc 674 must keep marking the player's actual location.
		player.getStatus().setVarC(WorldMap.CENTER_POSITION_VAR, message.getPosition().toPackedInt());
	}
}
