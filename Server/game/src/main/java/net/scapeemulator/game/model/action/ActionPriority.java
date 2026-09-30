package net.scapeemulator.game.model.action;

/**
 * if(topActions != empty) exec(topactions) else exec(rootAction)
 * 
 * exec(action_ : actions) action.exec()
 * 
 * if action_.enabled(cease) break; ???
 * 
 * else continue
 * 
 * @author Teemu
 *
 */
public enum ActionPriority {
	TOP, PRIMARY, SECONDARY;
}
