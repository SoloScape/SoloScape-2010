package net.scapeemulator.game.model.player;

import net.scapeemulator.game.cache.ItemDefinition;
import net.scapeemulator.game.model.constants.GenericValue;
import net.scapeemulator.game.model.def.EquipmentDefinition;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.mob.Animation;

public final class Equipment {
	public static final int HEAD = 0;
	public static final int CAPE = 1;
	public static final int NECK = 2;
	public static final int WEAPON = 3;
	public static final int BODY = 4;
	public static final int SHIELD = 5;
	public static final int LEGS = 7;
	public static final int HANDS = 9;
	public static final int FEET = 10;
	public static final int RING = 12;
	public static final int AMMO = 13;

	public static void remove(Player player, int slot) {
		Inventory inventory = player.getInventories().get(Inventory.BACKPACK);
		Inventory equipment = player.getInventories().get(Inventory.EQUIPMENT);

		Item item = equipment.get(slot);
		if (item == null)
			return;

		Item remaining = inventory.add(item);
		equipment.set(slot, remaining);
		if (remaining == null)
			removeBonus(player, item);

		if (slot == WEAPON && remaining == null) {
			weaponChanged(player, equipment.get(WEAPON), equipment.get(SHIELD));
		} else if (slot == SHIELD && remaining == null) {
			shieldChanged(player, equipment.get(WEAPON), equipment.get(SHIELD));
		}
	}

	// TODO: rewrite/clean up
	public static void equip(Player player, int slot) {
		Inventory inventory = player.getInventories().get(Inventory.BACKPACK);
		Inventory equipment = player.getInventories().get(Inventory.EQUIPMENT);
		Item originalWeapon = equipment.get(WEAPON);
		Item originalShield = equipment.get(SHIELD);

		Item item = inventory.get(slot);
		if (item == null)
			return;

		EquipmentDefinition def = item.getEquipmentDefinition();
		if (def == null)
			return;

		int targetSlot = def.getSlot();

		boolean unequipShield = def.getSlot() == WEAPON && def.isTwoHanded() && equipment.get(SHIELD) != null;
		boolean unequipWeapon = targetSlot == SHIELD && equipment.get(WEAPON) != null
				&& equipment.get(WEAPON).getEquipmentDefinition().isTwoHanded();
		boolean topUpStack = item.getDefinition().isStackable() && item.getId() == equipment.get(targetSlot).getId();
		boolean drainStack = equipment.get(targetSlot) != null
				&& equipment.get(targetSlot).getDefinition().isStackable()
				&& inventory.contains(equipment.get(targetSlot).getId());

		if ((unequipShield || unequipWeapon) && inventory.freeSlots() == 0) {
			inventory.fireCapacityExceeded();
			return;
		}

		if (topUpStack) {
			Item remaining = equipment.add(item);
			inventory.set(slot, remaining);
		} else {
			if (drainStack) {
				Item remaining = inventory.add(equipment.get(targetSlot));

				equipment.set(targetSlot, remaining);
				if (remaining != null) {
					return;
				} else {
					removeBonus(player, remaining);
				}
			}

			inventory.remove(item, slot);

			Item other = equipment.get(targetSlot);
			if (other != null) {
				inventory.add(other);
				removeBonus(player, other);
			}
			addBonus(player, item);
			equipment.set(targetSlot, item);
		}

		if (unequipShield) {
			Item shield = equipment.get(SHIELD);
			Item remaining = inventory.add(shield);
			equipment.set(SHIELD, remaining);
			if (shield == null)
				removeBonus(player, shield);
		}
		if (unequipWeapon) {
			Item weapon = equipment.get(WEAPON);
			Item remaining = inventory.add(weapon);
			equipment.set(WEAPON, remaining);
			// removebonus if weapon null
			if (weapon == null)
				removeBonus(player, weapon);
		}

		Item weapon = equipment.get(WEAPON);
		Item shield = equipment.get(SHIELD);
		boolean weaponChanged = false;
		boolean shieldChanged = false;
		if (originalWeapon == null && weapon != null)
			weaponChanged = true;
		else if (weapon == null && originalWeapon != null)
			weaponChanged = true;
		else if (originalWeapon != null && weapon != null && originalWeapon.getId() != weapon.getId())
			weaponChanged = true;

		if (originalShield == null && shield != null)
			shieldChanged = true;
		else if (shield == null && originalShield != null)
			shieldChanged = true;
		else if (originalShield != null && shield != null && originalShield.getId() != shield.getId())
			shieldChanged = true;

		if (weaponChanged) {
			weaponChanged(player, weapon, shield);
		}
		if (shieldChanged) {
			shieldChanged(player, weapon, shield);
		}
	}

	private static void shieldChanged(Player player, Item weapon, Item shield) {
		WeaponType type = WeaponType.UNARMED;
		Animation defAnim = type.getDefenceAnimation();
		if (shield == null) {
			if (weapon != null) {
				ItemDefinition def = weapon.getDefinition();
				type = WeaponType.values()[def.getGeneric(GenericValue.WEAPON_TYPE, 0)];
				int id = def.getGeneric(GenericValue.DEFEND_ANIMATION, type.getDefenceAnimation().getId());
				if (type.getDefenceAnimation().getId() != id) {
					defAnim = new Animation(id);
				}
			}
		} else {
			defAnim = new Animation(shield.getDefinition().getGeneric(GenericValue.DEFEND_ANIMATION, 12035));
		}
		player.getCombat().setDefAnim(defAnim);
	}

	private static void weaponChanged(Player player, Item weapon, Item shield) {
		WeaponType type = WeaponType.UNARMED;
		int speed = type.getAttackSpeed(), stance = 1426;
		Animation defAnim = type.getDefenceAnimation();
		Animation[] attackAnimations = type.getAttackAnimations();
		if (weapon != null) {
			ItemDefinition def = weapon.getDefinition();
			type = WeaponType.values()[def.getGeneric(GenericValue.WEAPON_TYPE, 0)];
			speed = def.getGeneric(GenericValue.ATTACK_SPEED, type.getAttackSpeed());
			stance = def.getGeneric(GenericValue.RENDER_PACK, stance);
			int defAnim_ = def.getGeneric(GenericValue.DEFEND_ANIMATION, type.getDefenceAnimation().getId());
			attackAnimations = def.getAttackAnimations(type.getAttackAnimations());
			if (defAnim_ != defAnim.getId()) {
				defAnim = new Animation(defAnim_);
			}
		}

		if (shield != null) {
			defAnim = new Animation(shield.getDefinition().getGeneric(GenericValue.DEFEND_ANIMATION, 12035));
		}

		int prevStyles = player.getCombat().getStyles().length, newStyles = type.getStyles().length;

		player.setStance(stance);

		player.getCombat().setAttackStyles(speed, type.getStyles());
		player.getCombat().setAttackAnimations(attackAnimations);
		player.getCombat().setDefAnim(defAnim);

		player.getStatus().setStatus("using_special", false);

		if (newStyles != prevStyles && player.getCombat().getCurrentStyle() == prevStyles - 1) {
			player.getStatus().setStatus("attack_style", newStyles - 1);
		}
	}

	public static void addBonus(Player player, Item item) {
		ItemDefinition definition = item.getDefinition();
		for (int i = 0; i < 5; i++) {
			player.getBonus().addAgressive(i, definition.getGeneric(GenericValue.OFFENSIVE_BONUS_SLASH + i, 0));
		}
		for (int i = 0; i < 5; i++) {
			player.getBonus().addDefensive(i, definition.getGeneric(GenericValue.DEFENSIVE_BONUS_SLASH + i, 0));
		}
		for (int i = 0; i < 4; i++) {
			player.getBonus().addExtra(i, definition.getGeneric(GenericValue.BONUS_STRENGTH + i, 0));
		}
	}

	public static void removeBonus(Player player, Item item) {
		ItemDefinition definition = item.getDefinition();
		for (int i = 0; i < 5; i++) {
			player.getBonus().removeAgressive(i, definition.getGeneric(GenericValue.OFFENSIVE_BONUS_SLASH + i, 0));
		}
		for (int i = 0; i < 5; i++) {
			player.getBonus().removeDefensive(i, definition.getGeneric(GenericValue.DEFENSIVE_BONUS_SLASH + i, 0));
		}
		for (int i = 0; i < 4; i++) {
			player.getBonus().removeExtra(i, definition.getGeneric(GenericValue.BONUS_STRENGTH + i, 0));
		}
	}

	private Equipment() {
		/* empty */
	}
}
