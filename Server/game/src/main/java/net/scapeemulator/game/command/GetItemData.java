package net.scapeemulator.game.command;

import java.util.Arrays;

import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.model.player.Player;

public final class GetItemData extends CommandHandler {

	public GetItemData() {
		super("idata");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;
		try {
			int itemId = Integer.parseInt(arguments[0]);
			ItemDefinition def = ItemDefinition.forId(itemId);
			player.sendMessage("[" + def.getName() + "/" + itemId + "] itemConstants: "
					+ Arrays.toString(def.getGenerics().entrySet().toArray()));
		} catch (Exception e) {
			player.sendMessage("Oops! Something went wrong.", 99);
		}
	}
}
