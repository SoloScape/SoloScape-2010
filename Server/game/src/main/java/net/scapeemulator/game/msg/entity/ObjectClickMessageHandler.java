package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.map.MapArea;
import net.scapeemulator.game.model.map.WorldMap;
import net.scapeemulator.game.model.player.Player;

public class ObjectClickMessageHandler extends MessageHandler<Player, ObjectClickMessage> {

	@Override
	public void handle(Player player, ObjectClickMessage message) {
		int xTileGlobal = message.getX();
		int yTileGlobal = message.getY();
		MapArea region = null;
		int xArea = 0;
		int yArea = 0;
		if (region == null) {
			xArea = xTileGlobal >> 6;
			yArea = yTileGlobal >> 6;
			region = WorldMap.getRegion(xArea, yArea);
		}
		// GameMapObject object = null;
		// if (object != null) {
		// player.sendMessage("Exist check success! " + object.toString() + "
		// [option=" + message.getOption() + "]",
		// 99);
		// }
	}
}
