package net.scapeemulator.game.model.hud.trigger;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.Trigger;
import net.scapeemulator.game.msg.interf.util.ScriptMessage;

public class SplitChatTrigger extends Trigger<Boolean> {

	@Override
	public void trigger(Player player, Boolean splitchat) {
		if (splitchat) {
			player.send(new ScriptMessage(83, ""));
		}
	}
}
