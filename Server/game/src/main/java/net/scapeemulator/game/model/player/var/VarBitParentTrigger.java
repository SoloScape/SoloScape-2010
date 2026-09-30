package net.scapeemulator.game.model.player.var;

import java.util.List;

import net.scapeemulator.game.model.player.Player;

public class VarBitParentTrigger extends Trigger<Integer> {
	private List<VarBitVariable<?>> childs;

	public VarBitParentTrigger(int id, String name, List<VarBitVariable<?>> childs) {
		this.childs = childs;
	}

	@Override
	public void trigger(Player player, Integer current) {
		for (VarBitVariable<?> var : childs) {
			if (var.getTrigger() != null) {
				var.getTrigger().stateChanged(player, var.parse(current));
			}
		}
	}
}
