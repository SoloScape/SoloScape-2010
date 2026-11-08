package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.action.AttackNpcAction;

public final class NpcOptionTwoMessageHandler extends MessageHandler<Player, NpcOptionTwoMessage> {
	@Override
	public void handle(Player player, NpcOptionTwoMessage message) {
		if (player.dead() || player.blockingAction())
			return;
		// Resolve only NPCs actually sent to this player, never a client-supplied type ID.
		for (Npc npc : player.getLocalNpcs()) {
			if (npc.getId() == message.getNpcIndex() && AttackNpcAction.isValidTarget(player, npc)) {
				player.stopAction();
				player.getDisplay().closeInterfaces();
				player.getMovement().reset();
				player.startAction(new AttackNpcAction(player, npc, message.isRunning()));
				return;
			}
		}
	}
}
