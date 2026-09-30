package net.scapeemulator.game.model.player.var;

import java.util.HashMap;
import java.util.Map;

public class CVariable {
	protected static final Map<Integer, Trigger<Integer>> triggers = new HashMap<>();

	public static void registerTrigger(int id, Trigger<Integer> trigger) {
		triggers.put(id, trigger);
	}

	public static Trigger<Integer> getTrigger(int id) {
		return triggers.get(id);
	}
}
