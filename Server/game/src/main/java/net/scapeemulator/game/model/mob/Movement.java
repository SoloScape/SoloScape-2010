package net.scapeemulator.game.model.mob;

import java.util.ArrayDeque;
import java.util.Deque;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;

public final class Movement {
	private final Mob mob;
	private final Deque<Position> points = new ArrayDeque<>();
	private boolean runningQueue;
	private boolean minimapFlagReset = false;

	public Movement(Mob mob) {
		this.mob = mob;
	}

	public void reset() {
		points.clear();
		runningQueue = false;
		minimapFlagReset = true;
		mob.setInteractingTarget(null);
		mob.getCombat().setTarget(null);
	}

	public void addFirstStep(Position position) {
		points.clear();
		runningQueue = false;
		addStepImpl(position, mob.getPosition());
	}

	public void addStep(Position position) {
		addStepImpl(position, points.peekLast());
	}

	private void addStepImpl(Position position, Position last) {
		int deltaX = position.getX() - last.getX();
		int deltaY = position.getY() - last.getY();

		int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));

		for (int i = 0; i < max; i++) {
			if (deltaX < 0)
				deltaX++;
			else if (deltaX > 0)
				deltaX--;

			if (deltaY < 0)
				deltaY++;
			else if (deltaY > 0)
				deltaY--;
			points.add(new Position(position.getX() - deltaX, position.getY() - deltaY, position.getHeight()));
		}
	}

	public void tick() {
		Position position = mob.getPosition();
		if (!mob.teleporting)
			mob.setStartPosition(mob.getPosition());
		Position walk = points.poll();
		Position run = null;
		if (walk != null) {
			position = walk;

			boolean canRun = !(mob instanceof Player) || ((Player) mob).hasRunEnergy();
			if ((runningQueue || mob.isRunning()) && canRun) {
				run = points.poll();
				if (mob instanceof Player && run != null) {
					int deltaX = run.getX() - mob.getPosition().getX();
					int deltaY = run.getY() - mob.getPosition().getY();
					// The client can encode only the outer ring of two-tile offsets.
					// Walk tight corners instead of borrowing a third, unconsumed tile.
					if (Math.abs(deltaX) != 2 && Math.abs(deltaY) != 2) {
						points.addFirst(run);
						run = null;
					}
				}
				if (run != null)
					position = run;
			}
		}
		if (!mob.getPosition().equals(position))
			mob.setPosition(position);
		mob.setSteps(walk, run);
		if (mob instanceof Player)
			((Player) mob).updateRunEnergy(run != null);
	}

	public boolean isRunningQueue() {
		return runningQueue;
	}

	public void setRunningQueue(boolean runningQueue) {
		this.runningQueue = runningQueue;
	}

	public boolean isMinimapFlagReset() {
		return minimapFlagReset;
	}

	public void setMinimapFlagReset(boolean minimapFlagReset) {
		this.minimapFlagReset = minimapFlagReset;
	}
}
