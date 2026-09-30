package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.model.def.EquipmentDefinition;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.player.Appearance;
import net.scapeemulator.game.model.player.Equipment;
import net.scapeemulator.game.model.player.Gender;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerBlock;

public final class AppearancePlayerBlock extends PlayerBlock {
	private final String username;
	private final Appearance appearance;
	private final Inventory equipment;
	private final int stance, combat, skill;
	private final int prayerIcon;
	private int npcType = -1;

	public AppearancePlayerBlock(Player player) {
		super(0x20);
		this.username = player.getUsername();
		this.appearance = player.getAppearance();
		this.equipment = new Inventory(player.getInventories().get(Inventory.EQUIPMENT));
		this.stance = player.getStance();
		this.combat = player.getSkillSet().getCombatLevel();
		this.skill = player.getSkillSet().getTotalLevel();
		this.prayerIcon = player.getHeadIcon();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		Gender gender = appearance.getGender();
		PacketBuilder propertiesBuilder = new PacketBuilder(builder.getAllocator());

		/*
		 * flags field:
		 * 
		 * bit 0 - gender (0 = male, 1 = female)
		 * 
		 * bit 1 - Display name
		 * 
		 * bit 2 - show skill level instead of combat level
		 * 
		 * bit 3 - 5 - "Size" on tiles
		 * 
		 * bit 6 - 7 - second type of title?
		 */
		int flags = gender.ordinal();
		int size = 1;
		flags |= ((size - 1) << 3);
		propertiesBuilder.put(DataType.BYTE, flags);
		propertiesBuilder.put(DataType.BYTE, -1); // title, iirc
		propertiesBuilder.put(DataType.BYTE, -1); // TODO: pk icon
		propertiesBuilder.put(DataType.BYTE, prayerIcon);
		Item item;
		if (npcType != -1) {
			propertiesBuilder.put(DataType.SHORT, -1);
			propertiesBuilder.put(DataType.SHORT, npcType);
			propertiesBuilder.put(DataType.BYTE, 0); // team
		} else {
			for (int i = 0; i < 12; i++) {
				item = equipment.get(i);
				if (item != null && !noEquip(i)) {
					propertiesBuilder.put(DataType.SHORT, 0x8000 | ItemDefinition.forId(item.getId()).getEquipmentId());
				} else {
					int app = getAppearance(i);
					if (app != -1) {
						propertiesBuilder.put(DataType.SHORT, 0x100 | app);
					} else {
						propertiesBuilder.put(DataType.BYTE, 0);
					}
				}
			}
		}

		for (int i = 0; i < 5; i++) {
			propertiesBuilder.put(DataType.BYTE, appearance.getColor(i));
		}

		propertiesBuilder.put(DataType.SHORT, stance);
		propertiesBuilder.putString(username);
		propertiesBuilder.put(DataType.BYTE, combat);
		if ((flags & 0x4) != 0) {
			propertiesBuilder.put(DataType.SHORT, skill);
		} else {
			propertiesBuilder.put(DataType.BYTE, 0);
			propertiesBuilder.put(DataType.BYTE, 0);
		}
		propertiesBuilder.put(DataType.BYTE, 0);
		/*
		 * if the above byte is non-zero,
		 * 
		 * four unknown shorts are written (sound effects?)
		 */

		builder.put(DataType.BYTE, propertiesBuilder.getLength());
		builder.putRawBuilder(propertiesBuilder);
	}

	private boolean noEquip(int i) {
		if (i == 6 || i == 8 || i == 11)
			return true;
		return false;
	}

	private int getAppearance(int id) {
		boolean fullHelm = false, fullMask = false;
		switch (id) {
		case 4:
			return appearance.getStyle(2);
		case 6:
			boolean fullBody = false;
			Item item = equipment.get(Equipment.BODY);
			if (item != null)
				fullBody = EquipmentDefinition.forId(item.getId()).isFullBody();// sleeves?
			if (!fullBody) {
				return appearance.getStyle(3);// Possibly sleeves
			}
			return -1;
		case 7:
			return appearance.getStyle(5);
		case 8:
			item = equipment.get(Equipment.HEAD);
			if (item != null) {
				fullHelm = EquipmentDefinition.forId(item.getId()).isFullHelm();
				fullMask = EquipmentDefinition.forId(item.getId()).isFullMask();
			}
			if (!fullHelm && !fullMask) {
				return appearance.getStyle(0);
			}
			return -1;
		case 9:
			return appearance.getStyle(4);
		case 10:
			return appearance.getStyle(6);
		case 11:
			Gender gender = appearance.getGender();
			item = equipment.get(Equipment.HEAD);
			if (item != null) {
				fullHelm = EquipmentDefinition.forId(item.getId()).isFullHelm();
				fullMask = EquipmentDefinition.forId(item.getId()).isFullMask();
			}
			if (gender == Gender.MALE && !fullMask && !fullHelm) {
				return appearance.getStyle(1);
			}
			return -1;
		default:
			return -1;
		}
	}
}
