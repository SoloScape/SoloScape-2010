package net.scapeemulator.game.task.impl;

import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.task.Task;

public class SetVarBitTask extends Task {

	private final int varId, value;
	private final Player player;

	public SetVarBitTask(Player player, int varId, int value) {
		super(1, true);
		this.player = player;
		this.varId = varId;
		this.value = value;
	}

	@Override
	public void execute() {
		player.getStatus().setVarBit(varId, value);
	}
}
