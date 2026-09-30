package net.scapeemulator.game.cache;

import net.scapeemulator.cache.Archive;
import net.scapeemulator.cache.Cache;
import net.scapeemulator.cache.Container;
import net.scapeemulator.cache.ReferenceTable;
import net.scapeemulator.cache.util.ByteBufferUtils;
import net.scapeemulator.game.model.constants.GenericValue;
import net.scapeemulator.game.model.mob.Animation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class that loads item information from the cache. Removed most of the
 * unnecessary details, need to finalize the data. In future, we use a different
 * format.
 * 
 * @author Graham
 * @author `Discardedx2
 * @author Teemuzz
 * 
 *         TODO Finish some of the opcodes.
 * 
 */
public final class ItemDefinition {
	public static final String[] DEFAULT_GROUND_OPTIONS = new String[] { null, null, "take", null, null };
	public static final String[] DEFAULT_INVENTORY_OPTIONS = new String[] { null, null, null, null, "drop" };
	private String name;

	private boolean stackable;
	private int value;
	private boolean members;

	private int maleWearModel1 = -1;
	private int maleWearModel2 = -1;

	private String[] groundOptions;
	private String[] inventoryOptions;

	private boolean exchangable;

	private int noteBase = -1;
	private int noteTemplate = -1;
	private int teamId;
	private int lentBase = -1;
	private int lendTemplate = -1;

	private static final Logger logger = LoggerFactory.getLogger(ItemDefinition.class);
	private static ItemDefinition[] definitions;
	private static int wearableItemCount = 0;

	Map<Integer, Object> generics;
	private int equipmentId;
	private Animation[] attackAnimations;
	// TODO: bonus, how

	public static void init(Cache cache, boolean loadMembers) throws IOException {
		int count = 0;

		Container tableContainer = Container.decode(cache.getStore().read(255, 19));
		ReferenceTable table = ReferenceTable.decode(tableContainer.getData());

		int files = table.capacity();
		definitions = new ItemDefinition[files * 256];

		for (int file = 0; file < files; file++) {
			ReferenceTable.Entry entry = table.getEntry(file);
			if (entry == null)
				continue;

			Archive archive = Archive.decode(cache.read(19, file).getData(), entry.size());
			int nonSparseMember = 0;
			for (int member = 0; member < entry.capacity(); member++) {
				ReferenceTable.ChildEntry childEntry = entry.getEntry(member);
				if (childEntry == null)
					continue;

				int id = file * 256 + member;
				definitions[id] = ItemDefinition.decode(archive.getEntry(nonSparseMember++));
				parseNonClientData(id);
				postParse(definitions[id], loadMembers);
				count++;
			}
		}
		for (ItemDefinition def : definitions) {
			if (def != null) {
				if ((def.getNoteTemplate() != -1 || def.getLendTemplate() != -1)
						|| (def.isMembersOnly() && !loadMembers)) {
					postParse(def, loadMembers);
				}
			}
		}
		logger.info("Loaded " + count + " item definitions. Total of wearable items: " + wearableItemCount);
	}

	private static void postParse(ItemDefinition def, boolean loadMembers) {
		if (def.getNoteTemplate() != -1) {
			ItemDefinition base = definitions[def.getNoteBase()];
			def.stackable = true;
			def.value = base.value;
			def.name = base.name;
			def.members = base.members;
			def.exchangable = base.exchangable;
		}
		if (def.getLendTemplate() != -1) {
			ItemDefinition base = definitions[def.getLendBase()];
			def.name = base.name;
			def.teamId = base.teamId;
			def.generics = base.generics;
			def.maleWearModel1 = base.maleWearModel1;
			def.maleWearModel2 = base.maleWearModel2;
			def.members = base.members;
			def.groundOptions = base.groundOptions;
			def.value = 0;
			def.exchangable = false;
			def.inventoryOptions = new String[5];
			for (int i = 0; i < 4; i++) {
				def.inventoryOptions[i] = base.inventoryOptions[i];
			}
			def.inventoryOptions[4] = "Discard";
		}
		if (def.members && !loadMembers) {
			def.exchangable = false;
			def.value = 0;
			def.groundOptions = DEFAULT_GROUND_OPTIONS;
			def.inventoryOptions = DEFAULT_INVENTORY_OPTIONS;
			def.generics = null;// TODO:
		}
		if (isEquipment(def)) {
			def.equipmentId = wearableItemCount++;
		}
	}

	private static boolean isEquipment(ItemDefinition definition) {
		return definition.maleWearModel1 >= 0 || definition.maleWearModel2 >= 0;
	}

	private static void parseNonClientData(int id) {
		ItemDefinition def = definitions[id];
		switch (id) {
		case 4151:
			def.generics.put(GenericValue.OFFENSIVE_BONUS_SLASH, 82);
			def.generics.put(GenericValue.BONUS_STRENGTH, 82);
			break;
		case 11694:
			def.generics.put(GenericValue.OFFENSIVE_BONUS_CRUSH, 80);
			def.generics.put(GenericValue.OFFENSIVE_BONUS_SLASH, 132);
			def.generics.put(GenericValue.BONUS_STRENGTH, 132);
			break;
		case 14484:
			def.generics.put(GenericValue.OFFENSIVE_BONUS_STAB, 41);
			def.generics.put(GenericValue.OFFENSIVE_BONUS_SLASH, 57);
			def.generics.put(GenericValue.OFFENSIVE_BONUS_CRUSH, -4);
			
			def.generics.put(GenericValue.DEFENSIVE_BONUS_STAB, 13);
			def.generics.put(GenericValue.DEFENSIVE_BONUS_SLASH, 26);
			def.generics.put(GenericValue.DEFENSIVE_BONUS_CRUSH, 7);
			
			def.generics.put(GenericValue.BONUS_STRENGTH, 56);
			break;
		case 11235:
			def.generics.put(GenericValue.ATTACK_SPEED, 9);
			break;
		case 732: // Holy water
		case 6522: // Toktz-xil-ul
		case 10501: // Snowball
		case 11951: // Snowball
			break;
		case 800: // Bronze thrownaxe
		case 801: // Iron thrownaxe
		case 802: // Steel thrownaxe
		case 803: // Mithril thrownaxe
		case 804: // Adamant thrownaxe
		case 805: // Rune thrownaxe
		case 13721: // Performance throwing axe
		case 13883: // Morrigan's throwing axe
		case 13957: // C. morrigan's throwing axe
			def.generics.put(GenericValue.ATTACK_SPEED, 5);
			break;
		case 825: // Bronze javelin
		case 826: // Iron javelin
		case 827: // Steel javelin
		case 828: // Mithril javelin
		case 829: // Adamant javelin
		case 830: // Rune javelin
		case 831: // Bronze javelin (p)
		case 832: // Iron javelin (p)
		case 833: // Steel javelin (p)
		case 834: // Mithril javelin (p)
		case 835: // Adamant javelin (p)
		case 836: // Rune javelin (p)
		case 5642: // Bronze javelin (p+)
		case 5643: // Iron javelin (p+)
		case 5644: // Steel javelin (p+)
		case 5645: // Mithril javelin (p+)
		case 5646: // Adamant javelin (p+)
		case 5647: // Rune javelin (p+)
		case 5648: // Bronze jav'n (p++)
		case 5649: // Iron javelin (p++)
		case 5650: // Steel javelin (p++)
		case 5651: // Mithril javelin (p++)
		case 5652: // Adamant javelin (p++)
		case 5653: // Rune javelin (p++)
		case 13879: // Morrigan's javelin
		case 13880: // Morrigan's javelin (p)
		case 13881: // Morrigan's javelin (p+)
		case 13882: // Morrigan's javelin (p++)
		case 13953: // Corrupt morrigan's javelin
		case 13954: // C. morrigan's javelin (p)
		case 13955: // C. morrigan's javelin (p+)
		case 13956: // C. morrigan's javelin (p++)
			def.generics.put(GenericValue.ATTACK_SPEED, 6);
			break;
		}
	}

	public static int count() {
		return definitions.length;
	}

	public static ItemDefinition forId(int id) {
		return definitions[id];
	}

	/**
	 *
	 * @param buffer
	 *            A {@link ByteBuffer} that contains information such as the
	 *            items location.
	 * @return a new ItemDefinition.
	 */
	@SuppressWarnings("unused")
	public static ItemDefinition decode(ByteBuffer buffer) {
		ItemDefinition def = new ItemDefinition();
		def.groundOptions = new String[] { null, null, "take", null, null };
		def.inventoryOptions = new String[] { null, null, null, null, "drop" };
		while (true) {
			int opcode = buffer.get() & 0xFF;
			if (opcode == 0)
				break;
			if (opcode == 1)
				buffer.getShort();
			else if (opcode == 2)
				def.name = ByteBufferUtils.getJagexString(buffer);
			else if (opcode == 4)
				buffer.getShort();
			else if (opcode == 5)
				buffer.getShort();
			else if (opcode == 6)
				buffer.getShort();
			else if (opcode == 7) {
				buffer.getShort();
			} else if (opcode == 8) {
				buffer.getShort();
			} else if (opcode == 11)
				def.stackable = true;
			else if (opcode == 12)
				def.value = buffer.getInt();
			else if (opcode == 16)
				def.members = true;
			else if (opcode == 18) {
				int i = buffer.getShort() & 0xFFFF;
			} else if (opcode == 23)
				def.maleWearModel1 = buffer.getShort() & 0xFFFFF;
			else if (opcode == 24)
				buffer.getShort();
			else if (opcode == 25)
				def.maleWearModel2 = buffer.getShort() & 0xFFFFF;
			else if (opcode == 26)
				buffer.getShort();
			else if (opcode >= 30 && opcode < 35)
				def.groundOptions[opcode - 30] = ByteBufferUtils.getJagexString(buffer);
			else if (opcode >= 35 && opcode < 40)
				def.inventoryOptions[opcode - 35] = ByteBufferUtils.getJagexString(buffer);
			else if (opcode == 40) {
				int length = buffer.get() & 0xFF;
				for (int index = 0; index < length; index++) {
					buffer.getShort();
					buffer.getShort();
				}
			} else if (opcode == 41) {
				int length = buffer.get() & 0xFF;
				for (int index = 0; index < length; index++) {
					buffer.getShort();
					buffer.getShort();
				}
			} else if (opcode == 42) {
				int length = buffer.get() & 0xFF;
				for (int index = 0; index < length; index++) {
					int i = buffer.get();
				}
			} else if (opcode == 65) {
				def.exchangable = true;
			} else if (opcode == 78) {
				buffer.getShort();
			} else if (opcode == 79) {
				buffer.getShort();
			} else if (opcode == 90) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 91) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 92) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 93) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 95) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 96) {
				int i = buffer.get() & 0xFF;
			} else if (opcode == 97) {
				def.noteBase = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 98) {
				def.noteTemplate = buffer.getShort() & 0xFFFFF;
			} else if (opcode >= 100 && opcode < 110) {
				// if (def.stackableIds == null) {
				// def.stackableIds = new int[10];
				// def.stackableAmounts = new int[10];
				// }
				// def.stackableIds[opcode - 100] =
				buffer.getShort();// & 0xFFFFF;
				// def.stackableAmounts[opcode - 100] =
				buffer.getShort();// & 0xFFFFF;
			} else if (opcode == 110) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 111) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 112) {
				int i = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 113) {
				int i = buffer.get();
			} else if (opcode == 114) {
				int i = buffer.get() * 5;
			} else if (opcode == 115) {
				def.teamId = buffer.get() & 0xFF;
			} else if (opcode == 121) {
				def.lentBase = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 122) {
				def.lendTemplate = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 125) {
				int i = buffer.get() << 0;
				int i2 = buffer.get() << 0;
				int i3 = buffer.get() << 0;
			} else if (opcode == 126) {
				int i = buffer.get() << 0;
				int i2 = buffer.get() << 0;
				int i3 = buffer.get() << 0;
			} else if (opcode == 127) {
				int i = buffer.get() & 0xFF;
				int i2 = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 128) {
				int i = buffer.get() & 0xFF;
				int i2 = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 129) {
				int i = buffer.get() & 0xFF;
				int i2 = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 130) {
				int i = buffer.get() & 0xFF;
				int i2 = buffer.getShort() & 0xFFFFF;
			} else if (opcode == 132) {
				int len = buffer.get() & 0xFF;
				for (int index = 0; index < len; index++) {
					int anInt = buffer.getShort() & 0xFFFFF;
				}
			} else if (opcode == 249) {
				int length = buffer.get() & 0xFF;
				def.generics = new HashMap<Integer, Object>(length);
				for (int index = 0; index < length; index++) {
					boolean stringInstance = buffer.get() == 1;
					int key = ByteBufferUtils.getTriByte(buffer);
					Object value = stringInstance ? ByteBufferUtils.getJagexString(buffer) : buffer.getInt();
					def.generics.put(key, value);
				}
			}
		}
		return def;
	}

	public Animation[] getAttackAnimations(Animation[] animations) {
		if (this.attackAnimations == null) {
			this.attackAnimations = new Animation[animations.length];
			for (int i = 0; i < animations.length; i++) {
				this.attackAnimations[i] = new Animation(
						getGeneric(GenericValue.ATTACK_ANIMATIONS[i], animations[i].getId()));
			}
		}
		return this.attackAnimations;
	}

	public String getGeneric(int key, String def) {
		if (generics == null || generics.get(key) == null)
			return def;
		return generics.get(key).toString();
	}

	public int getGeneric(int key, int def) {
		if (generics == null || generics.get(key) == null)
			return def;
		return (int) generics.get(key);
	}

	public Map<Integer, Object> getGenerics() {
		return generics;
	}

	public String getName() {
		return name;
	}

	public boolean isStackable() {
		return stackable;
	}

	public int getValue() {
		return value;
	}

	public boolean isMembersOnly() {
		return members;
	}

	public int getMaleWearModel1() {
		return maleWearModel1;
	}

	public int getMaleWearModel2() {
		return maleWearModel2;
	}

	public String[] getGroundOptions() {
		return groundOptions;
	}

	public String[] getInventoryOptions() {
		return inventoryOptions;
	}

	public boolean isExchangable() {
		return exchangable;
	}

	public int getNoteBase() {
		return noteBase;
	}

	public int getNoteTemplate() {
		return noteTemplate;
	}

	public int getTeamId() {
		return teamId;
	}

	public int getLendBase() {
		return lentBase;
	}

	public int getLendTemplate() {
		return lendTemplate;
	}

	public int getEquipmentId() {
		return equipmentId;
	}
}
