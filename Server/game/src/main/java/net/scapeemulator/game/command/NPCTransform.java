package net.scapeemulator.game.command;

import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.Player;

public class NPCTransform extends CommandHandler {

	public NPCTransform() {
		super("transform");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		try {
			if (arguments.length == 2) {
				Npc npc = World.getWorld().getNpcs().get(Integer.parseInt(arguments[0]));
				npc.transform(Integer.parseInt(arguments[1]));
			} else {
				player.sendMessage("Invalid arguments. Use transform [npcid] [defid]", 99);
			}
		} catch (Exception e) {
			player.sendMessage("Oops! something went wrong. Use transform [npcid] [defid]", 99);
		}
	}
}
