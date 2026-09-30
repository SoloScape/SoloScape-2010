package net.scapeemulator.cache.def;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.cache.util.ByteBufferUtils;

public class GameConstantContainer<T> {
	private final T defaultValue;
	private final Map<Integer, T> values;

	public GameConstantContainer(T defaultValue, Map<Integer, T> values) {
		this.defaultValue = defaultValue;
		this.values = values;
	}

	public T getDefaultValue() {
		return defaultValue;
	}
	
	public Map<Integer, T> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return "TypeDefinition [defaultValue=" + defaultValue + ", values="
				+ Arrays.toString(values.entrySet().toArray()) + "]";
	}

	public T getValue(int key) {
		T type = values.get(key);
		return type == null ? defaultValue : type;
	}

	@SuppressWarnings("unchecked")
	public static GameConstantContainer<?> decode(ByteBuffer buffer) {
		boolean str = false;
		String defaultStr = "";
		int defaultInt = 0;
		@SuppressWarnings("rawtypes")
		Map values = null;
		loop: while (true) {
			int opcode = (buffer.get() & 0xFF);
			switch (opcode) {
			case 0:
				break loop;
			case 1:
			case 2:
				buffer.get();
				break;
			case 3:
				defaultStr = ByteBufferUtils.getString(buffer);
				break;
			case 4:
				defaultInt = buffer.getInt() & 0xFFFFFFFF;
				break;
			case 5:
			case 6:
				int length = buffer.getShort() & 0xffff;
				if (opcode == 5) {
					values = new HashMap<Integer, String>();
					str = true;
					for (int i = 0; i < length; i++) {
						int key = buffer.getInt() & 0xFFFFFFFF;
						values.put(key, ByteBufferUtils.getString(buffer));
					}
				} else {
					values = new HashMap<Integer, Integer>();
					for (int i = 0; i < length; i++) {
						int key = buffer.getInt() & 0xFFFFFFFF;
						values.put(key, buffer.getInt() & 0xFFFFFFFF);
					}
				}
				break;
			default:
				System.out.println("Whoopsie!" + opcode);
			}
		}

		if (str) {
			if (values == null)
				values = new HashMap<Integer, String>();
			return new GameConstantContainer<String>(defaultStr, Collections.unmodifiableMap(values));
		} else {
			if (values == null)
				values = new HashMap<Integer, Integer>();
			return new GameConstantContainer<Integer>(defaultInt, Collections.unmodifiableMap(values));
		}
	}
}
