package net.scapeemulator.cache.def;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.cache.util.ByteBufferUtils;

public class InvType {

	private final int id, length;

	public InvType(int id, int length) {
		this.id = id;
		this.length = length;
	}

	@Override
	public String toString() {
		return "InvType [id=" + id + ", length=" + length + "]";
	}

	public static InvType decode(int id, ByteBuffer buffer) {
		int length = 0;
		loop: while (true) {
			int opcode = (buffer.get() & 0xFF);
			switch (opcode) {
			case 0:
				break loop;
			case 2:
				length = buffer.getShort();
				break;
			default:
				System.out.println("Whoopsie!" + opcode);
			}
		}
		return new InvType(id, length);
	}
}
