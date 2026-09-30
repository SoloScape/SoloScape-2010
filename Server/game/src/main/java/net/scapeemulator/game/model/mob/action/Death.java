package net.scapeemulator.game.model.mob.action;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.task.Task;

public class Death extends Task {
	private Mob mob;

	public Death(Mob mob) {
		super(7, false);
		this.mob = mob;
		mob.setInteractingTarget(null);
		mob.getCombat().setTarget(null);
		mob.playAnimation(mob.getCombat().getDeathAnimation());
	}

	@Override
	public void execute() {
		// TODO: Safe zones, respawn positions
		mob.dropLoot();
		mob.teleport(new Position(3200, 3400, 0));
		mob.restore();
		stop();
	}
}
