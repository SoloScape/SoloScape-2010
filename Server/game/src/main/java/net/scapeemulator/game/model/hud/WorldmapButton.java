package net.scapeemulator.game.model.hud;

import net.scapeemulator.game.model.hud.interf.ChildInterface;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.interf.action.ClickActionHandler;
import net.scapeemulator.game.model.hud.util.WorldMap;
import net.scapeemulator.game.model.player.Player;

public class WorldmapButton extends ChildInterface implements ClickActionHandler {

	public WorldmapButton(int childId) {
		super(childId);
		addOption(1, this, false);
	}

	@Override
	public void handleAction(Player player, ChildInterfaceClickAction action) {
		if (!player.getDisplay().openRootInterface(WorldMap.INSTANCE, false)) {
			player.sendMessage("Please close the interface you have open before opening world map.");
		}
	}
}
