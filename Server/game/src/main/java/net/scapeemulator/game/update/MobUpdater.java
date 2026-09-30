package net.scapeemulator.game.update;

import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.MobList;
import net.scapeemulator.game.model.player.Player;

public abstract class MobUpdater<T extends Mob> extends EntityUpdater {
	private final MobList<T> mobs;

	public MobUpdater(MobList<T> mobs) {
		this.mobs = mobs;
	}

	@Override
	public final void preProcess() {
		for (T mob : mobs) {
			/**
			 * TODO: Common updates (for players & npcs) go here, not anywhere
			 * else.
			 */

			/*
			 * Is this required?
			 */
			preprocess(mob);
			mob.processEffects();
			// TODO: Something else?
			/**
			 * Do NOT change the order of combat and hit update as this will mess up the special attacks!
			 */
			mob.updateHits();
			mob.getCombat().process();// ??
			try {
				mob.getMovement().tick();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public abstract void process(Player player);

	@Override
	public final void postProcess() {
		for (T mob : mobs) {
			mob.reset();
			/*
			 * Is this required?
			 */
			postProcess(mob);
		}
	}

	protected abstract void preprocess(T mob);

	protected abstract void postProcess(T mob);

	protected MobList<T> getMobs() {
		return mobs;
	}
}
