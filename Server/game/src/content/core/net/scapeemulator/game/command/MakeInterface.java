package net.scapeemulator.game.command;

import net.scapeemulator.game.model.hud.util.ItemCreationInterface;
import net.scapeemulator.game.model.player.Player;

public class MakeInterface extends CommandHandler {

	public MakeInterface() {
		super("makeitem");
	}

	/**
	 * VARC 
	 * 
	 * 754 amount of items
	 * 
	 * 755 itemId
	 * 
	 * 756 itemId2
	 * 
	 * 757 itemId3
	 * 
	 * 758 itemId4
	 * 
	 * 759 itemId5
	 *  
	 * 760 itemId6
	 * 
	 * varstr 131 title
	 */
	@Override
	public void handle(Player player, String[] arguments) {
		player.getDisplay().openInterface(new ItemCreationInterface(), 752, Integer.parseInt(arguments[0]), false);
	}
}
