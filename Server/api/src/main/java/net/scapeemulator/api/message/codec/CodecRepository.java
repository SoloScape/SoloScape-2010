package net.scapeemulator.api.message.codec;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.api.message.Message;

public final class CodecRepository {
	private final MessageDecoder<?>[] inCodecs = new MessageDecoder<?>[256];
	private final Map<Class<?>, MessageEncoder<?>> outCodecs = new HashMap<>();

	public MessageDecoder<?> get(int opcode) {
		return inCodecs[opcode];
	}

	@SuppressWarnings("unchecked")
	public <T extends Message> MessageEncoder<T> get(Class<T> clazz) {
		return (MessageEncoder<T>) outCodecs.get(clazz);
	}

	public void bind(MessageDecoder<?> decoder) {
		inCodecs[decoder.opcode] = decoder;
	}

	public void bind(MessageEncoder<?> encoder) {
		outCodecs.put(encoder.clazz, encoder);
	}
}
