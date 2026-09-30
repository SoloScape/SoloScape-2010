package net.scapeemulator.game.model.action;

public abstract class Action {
	private final ActionPriority priority;
	private final int flags;

	public Action(ActionPriority priority, ActionFlag... flags) {
		this.priority = priority;
		int _flags = 0;
		for (ActionFlag flag : flags) {
			_flags |= 1 << flag.ordinal();
		}
		this.flags = _flags;
	}

	public boolean enabled(ActionFlag flag) {
		return (flags & (1 << flag.ordinal())) != 0;
	}
}
