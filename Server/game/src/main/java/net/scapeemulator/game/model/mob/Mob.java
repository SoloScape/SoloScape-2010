package net.scapeemulator.game.model.mob;

import java.util.ArrayDeque;
import java.util.Queue;

import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.entity.Entity;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.action.Death;
import net.scapeemulator.game.model.mob.combat.Factor;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.task.Action;

public abstract class Mob extends Entity implements Comparable<Mob> {

	private boolean unlisted = false;
	protected int id;

	protected final Movement walkingQueue = new Movement(this);
	protected Direction mostRecentDirection = Direction.SOUTH;
	protected Position startPosition;
	protected boolean teleporting, targetUpdated, dead;
	private Position walk, run;

	protected SpotAnimation spotAnimation;
	protected Animation animation;
	protected Position orientationPosition;
	protected Mob interactingTarget;
	protected Hit primaryHit, secondaryHit;
	protected String shout;

	protected final SkillSet skillSet = new SkillSet();
	protected final Combat combat = new Combat(this);
	protected final Factor factor = new Factor();
	protected final Bonus bonus = new Bonus();

	protected Action<?> action;

	protected int headIcon = -1;

	private final Queue<Hit> hits = new ArrayDeque<>();

	protected int statRestoreCounter = 60, healthRestoreCounter = 60;

	public void addHit(Hit hit) {
		synchronized (hits) {
			hits.add(hit);
		}
	}

	public void dropLoot() {
		// TODO Auto-generated method stub
	}

	/**
	 * Is this a good name?
	 */
	public void processEffects() {
		// Action?
		/**
		 * TODO: poison, death
		 */
		if (statRestoreCounter <= 0) {
			statRestoreCounter += 60;
			for (int i = 0; i < skillSet.size(); i++) {
				if (i != 3 && i != 5 && i != 23) {
					skillSet.restore(id);
				}
			}
		}
		if (healthRestoreCounter <= 0) {
			healthRestoreCounter += 60;
			skillSet.restore(3);
		}
		statRestoreCounter--;
		healthRestoreCounter--;
	}

	/**
	 * Post-death restore
	 */
	public void restore() {
		skillSet.restore();
		factor.reset();

		interactingTarget = null;
		targetUpdated = true;
		dead = false;
	}

	public void updateHits() {
		primaryHit = getHit();
		secondaryHit = getHit();
		if (primaryHit != null)
			hit(primaryHit);
		if (secondaryHit != null)
			hit(secondaryHit);
	}

	private void hit(Hit hit) {
		int remaining = skillSet.getCurrentLevel(3);
		if (remaining == 0) {
			if (hit == primaryHit) {
				primaryHit = null;
			}
			secondaryHit = null;
			clearHits();
		} else {
			if (remaining < hit.getDamage()) {
				hit.setDamage(remaining);
			}
			if (!hit.isCosmetic()) {
				Mob yielder = hit.getYielder();
				if (yielder != null) {
					yielder.getCombat().postAttack(this, hit);
				}
				skillSet.damage(Skill.HITPOINTS, hit.getDamage());
			}
		}
		remaining = skillSet.getCurrentLevel(3);
		if (remaining == 0) {
			dead = true;
			World.getWorld().getTaskScheduler().schedule(new Death(this));
		}
	}

	public void clearHits() {
		synchronized (hits) {
			hits.clear();
		}
	}

	public Hit getHit() {
		synchronized (hits) {
			return hits.poll();
		}
	}

	public void startAction(Action<?> action) {
		// Blocking action cancels action ???
		if (this.action != null) {
			if (this.action.equals(action))
				return;

			stopAction();
		}
		this.action = action;
		World.getWorld().getTaskScheduler().schedule(action);
	}

	public void stopAction() {
		if (action != null) {
			Action<?> oldAction = action;
			action = null;
			oldAction.stop();
		}
	}

	public boolean blockingAction() {
		if (action == null)
			return false;
		return action.blocking();
	}

	public boolean isTeleporting() {
		return teleporting;
	}

	public void teleport(Position position) {
		this.position = position;
		this.teleporting = true;
		this.walkingQueue.reset();
	}

	public void setSteps(Position firstStep, Position secondStep) {
		this.walk = firstStep;
		this.run = secondStep;
		if (secondStep == null) {
			secondStep = firstStep;
			firstStep = position;
		}
		if (firstStep != null && secondStep != null) {
			mostRecentDirection = Direction.between(firstStep, secondStep);
		}
	}

	public void setStartPosition(Position startPosition) {
		if (this.startPosition == null) {
			this.startPosition = new Position(0, 0, 0);
		} else {
			this.startPosition = startPosition;
		}
	}

	public void reset() {
		primaryHit = null;
		secondaryHit = null;
		animation = null;
		spotAnimation = null;
		shout = null;
		teleporting = false;
		targetUpdated = false;
		walkingQueue.setMinimapFlagReset(false);
	}

	public abstract boolean isRunning();

	@Override
	public int compareTo(Mob other) {
		if (other.id < id)
			return 1;
		if (other.id > id)
			return -1;
		return 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isListed() {
		return !unlisted;
	}

	public void unlist() {
		unlisted = true;
	}

	public Movement getMovement() {
		return walkingQueue;
	}

	public Direction getMostRecentDirection() {
		return mostRecentDirection;
	}

	public SkillSet getSkillSet() {
		return skillSet;
	}

	public Bonus getBonus() {
		return bonus;
	}

	public Animation getAnimation() {
		return animation;
	}

	public SpotAnimation getSpotAnimation() {
		return spotAnimation;
	}

	public boolean isAnimationUpdated() {
		return animation != null;
	}

	public boolean isSpotAnimationUpdated() {
		return spotAnimation != null;
	}

	public void playAnimation(Animation animation) {
		this.animation = animation;
	}

	public void playSpotAnimation(SpotAnimation spotAnimation) {
		this.spotAnimation = spotAnimation;
		// todo secondary spotanim
	}

	public void orientateToPosition(Position orientationPosition) {
		this.orientationPosition = orientationPosition;
	}

	public void shout(String shout) {
		this.shout = shout;
	}

	public String getShout() {
		return shout;
	}

	public Position getOrientationPosition() {
		return orientationPosition;
	}

	public Position getWalkStep() {
		return walk;
	}

	public Position getRunStep() {
		return run;
	}

	public Position getStartStep() {
		return startPosition;
	}

	public Combat getCombat() {
		return combat;
	}

	public Factor getFactor() {
		return factor;
	}

	public boolean isTargetUpdated() {
		return targetUpdated;
	}

	public Mob getInteractingTarget() {
		return interactingTarget;
	}

	public void setInteractingTarget(Mob interactingTarget) {
		this.interactingTarget = interactingTarget;
		targetUpdated = true;
	}

	public Hit getPrimaryHit() {
		return primaryHit;
	}

	public Hit getSecondaryHit() {
		return secondaryHit;
	}

	public int getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(int headIcon) {
		this.headIcon = headIcon;
	}

	public boolean dead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}
}
