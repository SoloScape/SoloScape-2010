package net.scapeemulator.game.model.mob.combat;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.Item;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.player.Player;

/**
 * @author Teemu
 * @version 0.1.0
 */
public abstract class SpecialAttack {

	public static enum Type {
		ITEM, NPC
	}

	private static Map<Integer, SpecialAttack> items = new HashMap<>();

	public void init(Mob mob) {

	}
	/*
	 * TODO: Target, multiple targets, how?
	 */

	/**
	 * ALWAYS call super.startAttack()
	 * 
	 * @param attacker
	 * @param victim
	 */
	public void startAttack(Mob attacker, Mob victim) {
		if (attacker.getClass() == Player.class) {
			Player player = (Player) attacker;
			player.getStatus().setStatus("using_special", false);
			player.getStatus().decrement("special_attack_power", getDrain());
		}
	}

	public abstract void afterAttack(Mob attacker, Mob victim, int damage);

	public abstract int getDrain();

	public abstract SpecialAttack nextState();

	public abstract int getCombatDelay();

	public abstract double getMaxHit(double maxhit_raw);

	public abstract double modAccuracy(double chance);

	public boolean max() {
		return false;
	}

	public SpotAnimation getAttackGraphic() {
		return null;
	}

	public Animation getAttackAnimation() {
		return null;
	}

	public static SpecialAttack get(Mob mob) {
		if (mob.getClass() == Player.class) {
			Player player = (Player) mob;
			Inventory equipment = player.getInventories().get(Inventory.EQUIPMENT);
			Item weapon;
			if (equipment != null && (weapon = equipment.get(3)) != null) {
				return items.get(weapon.getId());
			}
			return null;
		} else
			return null;
	}

	public static void register(Type type, int id, SpecialAttack spec) {
		switch (type) {
		case ITEM:
			items.put(id, spec);
			break;
		case NPC:
			break;
		}
	}

	public void secondaryAttack(Mob mob, Mob target, boolean didHit, double chance, int damage, int maxhit) {

	}

	public int damage(int damage) {
		return damage;
	}
}
