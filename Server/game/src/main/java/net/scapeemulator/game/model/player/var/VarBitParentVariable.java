package net.scapeemulator.game.model.player.var;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VarBitParentVariable extends PVariable<Integer> {

	public static final class Builder extends PVariable.Builder<Object> {
		private final List<VarBitVariable<?>> childs;

		public Builder(String name) {
			super(name);
			this.childs = new ArrayList<>();
		}

		@Override
		public PVariable.Builder<Object> value(Object value) {
			childs.add((VarBitVariable<?>) value);
			return this;
		}

		@Override
		public void register() {
			if (type == null)
				throw new NullPointerException("Variable.Type must be set before registering.");
			if (type != Type.VARBIT)
				throw new IllegalArgumentException(
						"VarbitParentVariable can't be VARP Type right now! Use VARBIT instead!");
			varPlrLookup.put(id, name);
			List<VarBitVariable<?>> childs = Collections.unmodifiableList(this.childs);
			varPlayers.put(name, new VarBitParentVariable(name, id, childs, new VarBitParentTrigger(id, name, childs),
					type, persistence));
		}
	}

	private VarBitParentVariable(String name, int id, List<VarBitVariable<?>> childs, Trigger<Integer> trigger,
			Type type, Persistence persistence) {
		super(id, name, trigger, persistence, type, constructDefault(childs));
	}

	@Override
	public String toString() {
		return "VarBitParentVariable [getId()=" + getId() + ", getPersistence()=" + getPersistence() + ", getType()="
				+ getType() + ", getValues()=" + getValues() + ", getName()=" + getName() + ", getTrigger()="
				+ getTrigger() + ", getDefaultValue()=" + getDefaultValue() + "]";
	}

	/**
	 * All of my fucking yes
	 */
	private static Integer constructDefault(List<VarBitVariable<?>> childs) {
		int value = 0;
		for (VarBitVariable<?> var : childs) {
			value |= var.codeDefault();
		}
		return value;
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

	@Override
	public boolean hasKey(int status) {
		return false;
	}
}
