package net.scapeemulator.game.grandexchange.interf;

import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.PaneInterface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.ScriptMessage;

public class CommonPrices extends PaneInterface {
	public static final Interface INSTANCE = new CommonPrices();

	public CommonPrices() {
		super(885, InterfacePersistence.TRANSIENT);
	}

	@Override
	protected void handleAction(Player player, InterfaceAction action) {
		switch (action.getType()) {
		case OPEN_INTER:
			player.send(new ScriptMessage(1169, "isi", 1, "49 gp", 0));
			player.send(new ScriptMessage(1169, "isi", 3, "165 gp", 1));
			player.send(new ScriptMessage(1169, "isi", 5, "6 gp", 2));
			player.send(new ScriptMessage(1169, "isi", 7, "38 gp", 3));
			player.send(new ScriptMessage(1169, "isi", 9, "11 gp", 4));
			player.send(new ScriptMessage(1169, "isi", 11, "36 gp", 5));
			player.send(new ScriptMessage(1169, "isi", 13, "5 gp", 6));
			player.send(new ScriptMessage(1169, "isi", 15, "4 gp", 7));
			player.send(new ScriptMessage(1169, "isi", 17, "374 gp", 8));
			player.send(new ScriptMessage(1169, "isi", 19, "196 gp", 9));
			player.send(new ScriptMessage(1169, "isi", 21, "53 gp", 10));
			player.send(new ScriptMessage(1169, "isi", 23, "281 gp", 11));
			player.send(new ScriptMessage(1169, "isi", 25, "135 gp", 12));
			player.send(new ScriptMessage(1169, "isi", 27, "395 gp", 13));
			player.send(new ScriptMessage(1169, "isi", 29, "234 gp", 14));
			player.send(new ScriptMessage(1169, "isi", 31, "168 gp", 15));
			player.send(new ScriptMessage(3336, ""));
			break;
		default:
			break;
		}
	}
}
