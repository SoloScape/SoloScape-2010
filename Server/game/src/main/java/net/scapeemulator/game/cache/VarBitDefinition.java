package net.scapeemulator.game.cache;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.scapeemulator.cache.Archive;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.ReferenceTable;

/**
 * @author Teemu
 */
public class VarBitDefinition {
	private static final Logger logger = LoggerFactory.getLogger(VarBitDefinition.class);
	private static Map<Integer, VarBitDefinition> definitions;

	public static void init(Cache cache) throws IOException {
		int count = 0;

		Container tableContainer = Container.decode(cache.getStore().read(255, 22));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		int files = table.capacity();
		definitions = new HashMap<>();

		for (int file = 0; file < files; file++) {
			ReferenceTable.Entry entry = table.getEntry(file);
			if (entry == null)
				continue;

			Archive archive = Archive.decode(cache.read(22, file).getData(), entry.size());
			int nonSparseMember = 0;
			for (int member = 0; member < entry.capacity(); member++) {
				ReferenceTable.ChildEntry childEntry = entry.getEntry(member);
				if (childEntry == null)
					continue;

				int id = file * 1024 + member;
				VarBitDefinition definition = VarBitDefinition.decode(id, archive.getEntry(nonSparseMember++));
				definitions.put(id, definition);
				count++;
			}
		}
		definitions = Collections.unmodifiableMap(definitions);
		logger.info("Loaded " + count + " varbit definitions.");
	}

	private int varId;
	private int bottomBit;
	private int topBit;
	private int id;

	public VarBitDefinition(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "VarBit [id=" + id + ", varId=" + varId + ", bottomBit=" + bottomBit + ", topBit=" + topBit + "]";
	}

	public static VarBitDefinition decode(int id, ByteBuffer buffer) {
		VarBitDefinition result = new VarBitDefinition(id);
		while (true) {
			int opcode = buffer.get() & 0xFF;
			if (opcode == 0)
				break;
			if (opcode == 1) {
				result.varId = buffer.getShort() & 0xffff;
				result.bottomBit = buffer.get() & 0xff;
				result.topBit = buffer.get() & 0xff;
			}
		}
		return result;
	}

	public int getVarId() {
		return varId;
	}

	public int getBottomBit() {
		return bottomBit;
	}

	public int getTopBit() {
		return topBit;
	}

	public static VarBitDefinition forId(int id) {
		return definitions.get(id);
	}
}
