package net.scapeemulator.game.cache;

import net.scapeemulator.cache.Archive;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.util.ByteBufferUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;

public final class SceneryDefinition {
	private static final Logger logger = LoggerFactory.getLogger(SceneryDefinition.class);
	private static SceneryDefinition[] definitions;
	private final int id;
	private String name;
	private int width, height, collisionType;
	private boolean typ1, walkable;

	public SceneryDefinition(int id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private static SceneryDefinition decode(int id, ByteBuffer buffer) {
		SceneryDefinition def = new SceneryDefinition(id);
		def.name = null;
		def.width = 1;
		def.height = 1;
		def.walkable = false;
		def.typ1 = true;
		def.collisionType = 2;
		while (true) {
			int opcode = buffer.get() & 0xFF;
			if (opcode == 0)
				break;
			if (opcode >= 30 && opcode < 35) {
				ByteBufferUtils.getJagexString(buffer);
				continue;
			}
			if (opcode >= 150 && opcode < 155) {
				ByteBufferUtils.getJagexString(buffer);// Members option
				continue;
			}
			switch (opcode) {
			case 5:
				doShit(buffer);
			case 1:
				int k = buffer.get() & 0xff;
				for (int i = 0; i < k; i++) {
					buffer.get();
					int e = buffer.get() & 0xff;
					for (int j = 0; j < e; j++) {
						buffer.getShort();
					}
				}
				break;
			case 2:
				def.name = ByteBufferUtils.getJagexString(buffer);
				break;
			case 14:
				def.width = buffer.get() & 0xff;
				break;
			case 15:
				def.height = buffer.get() & 0xff;
				break;
			case 17:
				def.typ1 = false;
				def.collisionType = 0;
				break;
			case 18:
				def.typ1 = false;
				break;
			case 21:
			case 22:
			case 23:
			case 62:
			case 64:
			case 73:
			case 82:
			case 88:
			case 89:
			case 90:
			case 91:
			case 94:
			case 95:
			case 96:
			case 97:
			case 98:
			case 103:
			case 105:
				break;
			case 77:
			case 92:
				// VarDef
				buffer.getShort();// VarBit
				buffer.getShort();// VarP
				if (opcode == 92) {
					buffer.getShort();// Default
				}
				int size = buffer.get() & 0xff;
				for (int i = 0; i <= size; i++) {
					buffer.getShort();// VarDefId
				}
				break;
			case 78:
			case 99:
			case 100:
				buffer.getShort();
				buffer.get();
				break;
			case 79:
				buffer.getShort();
				buffer.getShort();
				buffer.get();
				size = buffer.get() & 0xff;
				for (int i = 0; i < size; i++)
					buffer.getShort();
				break;
			case 160:
				size = buffer.get() & 0xff;
				for (int i = 0; i < size; i++)
					buffer.getShort();
				break;
			case 106:
				size = buffer.get() & 0xff;
				for (int i = 0; i < size; i++) {
					buffer.getShort();
					buffer.get();
				}
				break;
			case 24:
			case 65:
			case 66:
			case 67:
			case 70:
			case 71:
			case 72:
			case 93:
			case 102:
			case 107:
				buffer.getShort();
				break;
			case 162:
				buffer.getInt();
				break;
			case 163:
				buffer.get();
				buffer.get();
				buffer.get();
				buffer.get();
				break;
			case 74:
				def.walkable = true;
				break;
			case 27:
				def.collisionType = 1;
				break;
			case 19:
			case 28:
			case 29:
			case 39:
			case 69:
			case 75:
			case 81:
			case 101:
			case 104:
				buffer.get();
				break;
			case 40:
			case 41:
				size = buffer.get() & 0xff;
				for (int i = 0; i < size; i++) {
					buffer.getShort();
					buffer.getShort();
				}
				break;
			case 42:
				size = buffer.get() & 0xff;
				for (int i = 0; i < size; i++) {
					buffer.get();
				}
				break;
			case 249:
				int length = buffer.get() & 0xFF;
				for (int index = 0; index < length; index++) {
					boolean stringInstance = buffer.get() == 1;
					int key = ByteBufferUtils.getTriByte(buffer);
					Object value = stringInstance ? ByteBufferUtils.getJagexString(buffer) : buffer.getInt();
				}
				break;
			default:
				System.out.println("UNKNOWN OPCODE!! " + id + " " + opcode);
				break;
			}
		}
		if (def.walkable) {
			def.collisionType = 0;
			def.typ1 = false;
		}
		return def;
	}

	private static void doShit(ByteBuffer buffer) {
		int o = buffer.get() & 0xff;
		for (int i = 0; i < o; i++) {
			buffer.get();
			int a = buffer.get();
			for (int e = 0; e < a; e++)
				buffer.getShort();
		}
	}

	public static int count() {
		return definitions.length;
	}

	public static void init(Cache cache) throws IOException {
		int count = 0;

		Container tableContainer = Container.decode(cache.getStore().read(255, 16));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		int files = table.capacity();
		definitions = new SceneryDefinition[files * 256];

		for (int file = 0; file < files; file++) {
			ReferenceTable.Entry entry = table.getEntry(file);
			if (entry == null)
				continue;

			Archive archive = Archive.decode(cache.read(16, file).getData(), entry.size());
			int nonSparseMember = 0;
			for (int member = 0; member < entry.capacity(); member++) {
				ReferenceTable.ChildEntry childEntry = entry.getEntry(member);
				if (childEntry == null)
					continue;

				int id = file * 256 + member;
				SceneryDefinition definition = SceneryDefinition.decode(id, archive.getEntry(nonSparseMember++));
				definitions[id] = definition;
				count++;
			}
		}
		logger.info("Loaded " + count + " Scenery definitions.");
	}

	public static SceneryDefinition forId(int id) {
		if (id >= definitions.length || id < 0)
			return null;
		return definitions[id];
	}

	public static SceneryDefinition[] getDefinitions() {
		return definitions;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCollisionType() {
		return collisionType;
	}

	public boolean isTyp1() {
		return typ1;
	}

	public boolean isWalkable() {
		return walkable;
	}
}
