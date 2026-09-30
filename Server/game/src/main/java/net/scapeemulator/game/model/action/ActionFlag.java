package net.scapeemulator.game.model.action;

/**
 * 
 * @author Teemu
 */
public enum ActionFlag {
	DISCARD_SECONDARY_TASKS, // death, defending (secondary = emote, etc)
	CEASE_LESSER_TASKS, // teleportation, primary, add_on_bottom
	ADD_ON_BOTTOM, // e.g. defending, eating runs over
	NO_OVERWRITE, // Death
	BIGGER_PRIORITY_CANCELS,// everything minor, e.g. combat, interface tasks

	/**
	 * Open interface: runs first, ceases next
	 * 
	 * Hits run (top) combat runs (top), poison doesn't run (primary), levels don't revive back (primary)
	 * Waiting interfaces don't run,
	 * 
	 * overwriting action causes interfaces to close
	 * 
	 * ceasing action causes the next action to come.
	 * 
	 * primary task? add_on_bottom, cease_lesser_tasks
	 * 
	 * death:
	 * 
	 * add_on_top, executes last, cancels damage
	 */
}
