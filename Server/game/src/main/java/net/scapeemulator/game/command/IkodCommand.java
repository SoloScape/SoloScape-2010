package net.scapeemulator.game.command;

import net.scapeemulator.game.command.CommandHandler;
import net.scapeemulator.game.model.hud.interf.PaneInterface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.util.ScriptMessage;

public class IkodCommand extends CommandHandler {

	public IkodCommand() {
		super("ikod");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		int keptAmount = 3, type = 0;
		int carriedWealth = 90001, riskedWealth = 9001;
		int graveStoneType = Integer.parseInt(arguments[0]);
		int keptItems[] = { 11694, 4151, 3140, -1 };
		player.getDisplay().open(new PaneInterface(102, InterfacePersistence.TRANSIENT), false);

		player.send(new ScriptMessage(118, "iiooooiisii1", type, keptAmount, keptItems[0], keptItems[1], keptItems[2],
				keptItems[3], Integer.parseInt(arguments[1]), Integer.parseInt(arguments[2]),
				"You're marked with a <col=ff3333>skull<col=ff981f>.", carriedWealth, riskedWealth, graveStoneType));
	}
}
