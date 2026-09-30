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
	private boolean peeked;

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

			if (runningQueue || mob.isRunning() || peeked) {
				run = points.poll();
				if (mob instanceof Player) {
					int deltaX = walk.getX() - mob.getPosition().getX();
					int deltaY = walk.getY() - mob.getPosition().getY();
					if (run != null && !peeked) {
						deltaX += run.getX() - walk.getX();
						deltaY += run.getY() - walk.getY();
						if ((deltaY != 2 && deltaY != -2) && (deltaX != 2 && deltaX != -2)) {
							Position thirdStep = points.peek();
							if (thirdStep != null) {
								deltaX += thirdStep.getX() - run.getX();
								deltaY += thirdStep.getY() - run.getY();
							}
						}
						if ((deltaY != 2 && deltaY != -2) && (deltaX != 2 && deltaX != -2)) {
							points.addFirst(run);
							run = null;
						}
					}
				}
				if (run != null)
					position = run;
				if (peeked) {
					mob.setStartPosition(walk);
				}
			}
		}
		if (peeked) {
			walk = run;
			run = null;
			peeked = false;
		}
		if (!mob.getPosition().equals(position))
			mob.setPosition(position);
		mob.setSteps(walk, run);
	}

	public Position peek() {
		Position next = points.peek();
		if (next == null)
			return null;
		peeked = true;
		return next;
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
