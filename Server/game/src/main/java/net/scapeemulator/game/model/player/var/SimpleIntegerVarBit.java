package net.scapeemulator.game.model.player.var;

public final class SimpleIntegerVarBit extends VarBitVariable<Integer> {

	public static final class Builder extends VarBitVariable.Builder<Integer> {

		public Builder(String name) {
			super(name);
		}

		@Override
		public VarBitVariable.Builder<Integer> value(Integer value) {
			throw new IllegalArgumentException("Simple integer variables can't provide values!");
		}

		@Override
		public VarBitVariable<Integer> register() {
			VarBitVariable<Integer> var = new SimpleIntegerVarBit(name, id, defaultValue, trigger);
			varBitLookup.put(id, name);
			varBits.put(name, var);
			return var;
		}
	}

	private SimpleIntegerVarBit(String name, int id, int defaultValue, Trigger<Integer> trigger) {
		super(id, name, defaultValue, trigger, null);
	}

	@Override
	public Integer getValue(int index) {
		return index;
	}

	@Override
	public int getIndex(Object value) {
		if (value instanceof Integer) {
			return ((Integer) value).intValue();
		}
		return 0;
	}
}
