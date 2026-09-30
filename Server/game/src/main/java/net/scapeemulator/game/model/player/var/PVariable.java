package net.scapeemulator.game.model.player.var;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO: Document!
 * 
 * @author Teemuzz
 * @author Major
 * 
 * @see <a href=
 *      http://www.rune-server.org/runescape-development/rs-503-client-and-server/informative-threads/601913-regarding-player-saving-various-unwelcome-variables-3.html#post4929532">
 *      this topic.</a href>
 *
 * @param <T>
 */
public class PVariable<T> {
	static final Map<String, PVariable<?>> varPlayers = new HashMap<>();
	static final Map<Integer, String> varPlrLookup = new HashMap<>();

	public static PVariable<?> getVarById(int id) {
		String varName = varPlrLookup.get(id);
		if (varName == null)
			return null;
		return varPlayers.get(varName);
	}

	public static class Builder<T> {
		protected final String name;

		public Builder(String name) {
			this.name = name;
		}

		protected int id = -1;
		protected Type type;
		protected Persistence persistence = Persistence.TRANSIENT;
		protected List<T> values = new ArrayList<>();
		protected Trigger<T> trigger;
		protected T defaultValue = null;

		public Builder<T> id(int id) {
			this.id = id;
			return this;
		}

		public Builder<T> type(Type type) {
			this.type = type;
			return this;
		}

		public Builder<T> persist() {
			persistence = Persistence.PERSISTENT;
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

		public void register() {
			if (type == null)
				throw new NullPointerException("Variable.Type must be set before registering.");
			if (defaultValue != null && !values.contains(defaultValue))
				throw new NullPointerException("Can't register a default that isn't registered in the values.");
			varPlrLookup.put(id, name);
			varPlayers.put(name, new PVariable<T>(name, id, trigger, type, persistence, defaultValue,
					Collections.unmodifiableList(values)));
		}
	}

	public enum Persistence {
		PERSISTENT, TRANSIENT;
	}

	public enum Type {
		VARBIT, VARP;
	}

	public static Builder<String> stringBuilder(String name) {
		return new Builder<>(name);
	}

	public static Builder<Integer> intBuilder(String name) {
		return new Builder<>(name);
	}

	private final int id;
	private final String name;
	private final Type type;
	private final T defaultValue;

	private final Persistence persistence;

	private Trigger<T> trigger;
	private List<T> values;

	protected PVariable(int id, String name, Trigger<T> trigger, Persistence persistence, Type type, T defaultValue) {
		this.id = id;
		this.name = name;
		this.trigger = trigger;
		this.persistence = persistence;
		this.type = type;
		this.defaultValue = defaultValue;
	}

	public PVariable(String name, int id, Trigger<T> trigger, Type type, Persistence persistence, T defaultValue,
			List<T> values) {
		this.name = name;
		this.id = id;
		this.trigger = trigger;
		this.type = type;
		this.persistence = persistence;
		this.defaultValue = defaultValue;
		this.values = values;
	}

	public int getId() {
		return id;
	}

	public Persistence getPersistence() {
		return persistence;
	}

	public Type getType() {
		return type;
	}

	public T getValue(int index) {
		if (index >= values.size() || index < 0) {
			return defaultValue;
		}
		T value = values.get(index);
		return value == null ? defaultValue : value;
	}

	public int getIndex(Object value) {
		int idx = values.indexOf(value);
		if (idx == -1)
			return values.indexOf(defaultValue);
		return idx;
	}

	public List<T> getValues() {
		return values;
	}

	public String getName() {
		return name;
	}

	public Trigger<T> getTrigger() {
		return trigger;
	}

	public boolean hasKey(int status) {
		try {
			return values.get(status) != null;
		} catch (Exception e) {
			return false;
		}
	}

	public T getDefaultValue() {
		return defaultValue;
	}
}