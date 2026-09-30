package net.scapeemulator.game.model.player.var;

/**
 * TODO Document, clean
 * 
 * @author Teemu
 *
 */
public final class SimpleIntegerVariable extends PVariable<Integer> {
	private final int min, max;

	public static final class Builder extends PVariable.Builder<Integer> {
		private int min = Integer.MIN_VALUE, max = Integer.MAX_VALUE;

		public Builder(String name) {
			super(name);
		}

		@Override
		public PVariable.Builder<Integer> value(Integer value) {
			throw new IllegalArgumentException("Simple integer variables can't provide values!");
		}

		public Builder minimumValue(Integer value) {
			min = value;
			return this;
		}

		public Builder maximumValue(Integer value) {
			max = value;
			return this;
		}

		@Override
		public void register() {
			if (type == null)
				throw new NullPointerException("Variable.Type must be set before registering.");
			if (type != Type.VARP)
				throw new IllegalArgumentException(
						"SimpleIntegerVariable can't be VARBIT Type right now! Use VARP instead!");
			varPlrLookup.put(id, name);
			varPlayers.put(name,
					new SimpleIntegerVariable(name, id, min, max, trigger, type, defaultValue, persistence));
		}
	}

	private SimpleIntegerVariable(String name, int id, int min, int max, Trigger<Integer> trigger, Type type,
			Integer defaultValue, Persistence persistence) {
		super(id, name, trigger, persistence, type, defaultValue);
		this.min = min;
		this.max = max;
	}

	@Override
	public Integer getValue(int index) {
		return index;
	}

	@Override
	public int getIndex(Object value) {
		if (value instanceof Integer) {
			int val = ((Integer) value).intValue();
			if (val < min)
				return min;
			if (val > max)
				return max;
			return val;
		}
		return 0;
	}

	@Override
	public boolean hasKey(int status) {
		return true;
	}
}
