package net.scapeemulator.game.model.player.action;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.pathfinder.PathFinder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.task.Action;

/** Approaches through collision before handing an NPC attack to the combat engine. */
public final class AttackNpcAction extends Action<Player> {
	private final Npc npc;
	private final boolean running;
	private Position previousPosition, previousTarget;

	public AttackNpcAction(Player player, Npc npc, boolean running) {
		super(player, 1, true);
		this.npc = npc;
		this.running = running;
	}

	public static boolean isValidTarget(Player player, Npc npc) {
		return !player.dead() && npc.isListed() && !npc.dead() && npc.isAttackable()
				&& player.getLocalNpcs().contains(npc)
				&& player.getPosition().getHeight() == npc.getPosition().getHeight()
				&& player.getPosition().isWithinDistance(npc.getPosition());
	}

	@Override
	public void execute() {
		// The scheduler can tick a cancelled action once before removing it.
		if (!isRunning())
			return;
		if (!isValidTarget(mob, npc)) {
			mob.getMovement().reset();
			stop();
			return;
		}
		if (PathFinder.isAdjacentReachable(mob, npc.getPosition())) {
			mob.getMovement().reset();
			stop();
			mob.getCombat().setTarget(npc);
			mob.setInteractingTarget(npc);
			return;
		}
		if (mob.getPosition().equals(previousPosition) && npc.getPosition().equals(previousTarget)) {
			mob.getMovement().reset();
			stop();
			mob.sendMessage("I can't reach that.");
			return;
		}
		previousPosition = mob.getPosition();
		previousTarget = npc.getPosition();
		mob.getMovement().reset();
		PathFinder.findPathToAdjacent(mob, npc.getPosition(), running);
	}
}
