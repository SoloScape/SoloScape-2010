package net.scapeemulator.game.model.player.var;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.scapeemulator.game.cache.VarBitDefinition;

/**
 * TODO: DOCUMENT
 */
public class VarBitVariable<T> {
	static final Map<String, VarBitVariable<?>> varBits = new HashMap<>();
	static final Map<Integer, String> varBitLookup = new HashMap<>();

	public static VarBitVariable<?> getVarById(int id) {
		String varName = varBitLookup.get(id);
		if (varName == null)
			return null;
		return varBits.get(varName);
	}

	public static class Builder<T> {
		protected final String name;

		public Builder(String name) {
			this.name = name;
		}

		protected int id = -1;
		protected List<T> values = new ArrayList<>();
		protected Trigger<T> trigger;
		protected T defaultValue = null;

		public Builder<T> id(int id) {
			this.id = id;
			return this;
		}

		public Builder<T> value(T value) {
			values.add(value);
			return this;
		}

		public Builder<T> setDefault(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}

		public Builder<T> setTrigger(Trigger<T> trigger) {
			this.trigger = trigger;
			return this;
		}

		public VarBitVariable<T> register() {
			if (defaultValue != null && !values.contains(defaultValue))
				throw new NullPointerException("Can't register a default that isn't registered in the values.");
			VarBitVariable<T> var = new VarBitVariable<T>(id, name, defaultValue, trigger,
					Collections.unmodifiableList(values));
			varBitLookup.put(id, name);
			varBits.put(name, var);
			return var;
		}
	}

	private final int id;
	private final String name;
	private final T defaultValue;
	private final Trigger<T> trigger;

	private List<T> values;

	public VarBitVariable(int id, String name, T defaultValue, Trigger<T> trigger, List<T> values) {
		super();
		this.id = id;
		this.name = name;
		this.defaultValue = defaultValue;
		this.trigger = trigger;
		this.values = values;
		System.out.println("DEBUG: " + this);
	}

	@Override
	public String toString() {
		return "VarBitVariable [id=" + id + ", name=" + name + ", defaultValue=" + defaultValue + ", trigger=" + trigger
				+ ", values=" + (values == null ? "null" : Arrays.toString(values.toArray())) + "]";
	}

	public int getIndex(Object value) {
		int idx = values.indexOf(value);
		if (idx == -1)
			return values.indexOf(defaultValue);
		return idx;
	}

	public T getValue(int index) {
		if (index >= values.size() || index < 0) {
			return defaultValue;
		}
		T value = values.get(index);
		return value == null ? defaultValue : value;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public T getDefaultValue() {
		return defaultValue;
	}

	public Trigger<T> getTrigger() {
		return trigger;
	}

	public List<T> getValues() {
		return values;
	}

	public int codeDefault() {
		int set = getIndex(defaultValue);
		VarBitDefinition varbitDef = VarBitDefinition.forId(id);
		if (varbitDef != null) {
			int bottomBit = varbitDef.getBottomBit();
			int max = Status.max_values[varbitDef.getTopBit() - bottomBit];
			if (0 > set || max < set) {
				set = 0;
			}
			max <<= bottomBit;
			return max & set << bottomBit;
		}
		return 0;
	}

	public T parse(int parentStatus) {
		VarBitDefinition varbitDef = VarBitDefinition.forId(id);
		int bottom = varbitDef.getBottomBit();
		int top = varbitDef.getTopBit();
		int max = Status.max_values[top - bottom];
		return getValue(parentStatus >> bottom & max);
	}
}
