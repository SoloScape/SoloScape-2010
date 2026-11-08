package net.scapeemulator.game.grandexchange.interf;

import net.scapeemulator.game.model.hud.interf.BlockingTab;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.PaneInterface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.ScriptMessage;

public class ItemSets extends PaneInterface {
	public static final Interface INSTANCE = new ItemSets();

	public ItemSets() {
		super(645, InterfacePersistence.TRANSIENT);
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.send(new ScriptMessage(676, ""));
			player.getDisplay().open(new ItemSetsInventory(), true);
			break;
		default:
			break;
		}
	}

	public static class ItemSetsInventory extends BlockingTab {

		public ItemSetsInventory() {
			super(644, InterfacePersistence.TRANSIENT);
		}

	}
}
