package net.scapeemulator.game.model.hud.trigger;

import java.util.Iterator;
import java.util.Map.Entry;

import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.hud.combat.PrayerTab;
import net.scapeemulator.game.model.mob.combat.Factor;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.combat.PrayerDetails;
import net.scapeemulator.game.model.player.var.Trigger;

//TODO Clean
public class PrayerTrigger extends Trigger<Integer> {
	public static final int PROT_MELEE = 1 << PrayerTab.PROTECT_FROM_MELEE;
	public static final int PROT_MAGIC = 1 << PrayerTab.PROTECT_FROM_MAGIC;
	public static final int PROT_RANGE = 1 << PrayerTab.PROTECT_FROM_RANGED;
	public static final int PROT_SUMMONING = 1 << PrayerTab.PROTECT_FROM_SUMMONING;
	public static final int RETRIBUTION = 1 << PrayerTab.RETRIBUTION;
	public static final int SMITE = 1 << PrayerTab.SMITE;
	public static final int REDEMPTION = 1 << PrayerTab.REDEMPTION;
	// TODO CURSES

	@Override
	public void trigger(Player player, Integer value) {
		// Loop through all, headicon + factors
		Factor factor = player.getFactor();
		factor.reset();
		for (int i = 0; i <= 26; i++) {
			// add drain
			PrayerDetails details = PrayerDetails.values()[i];
			if ((value & (1 << i)) != 0) {
				if (details.getFactors() != null)
					for (Iterator<Entry<Integer, Double>> iterator = details.getFactors().entrySet()
							.iterator(); iterator.hasNext();) {
						Entry<Integer, Double> type = iterator.next();
						switch (type.getKey()) {
						case Skill.ATTACK:
							factor.setAttack(type.getValue());
							break;
						case Skill.DEFENCE:
							factor.setDefence(type.getValue());
							break;
						case Skill.STRENGTH:
							factor.setStrength(type.getValue());
							break;
						case Skill.RANGED:
							factor.setRanged(type.getValue());
							break;
						case Skill.MAGIC:
							factor.setMagic(type.getValue());
							break;
						}
					}
			}
		}
		player.sendMessage(player.getFactor().toString(), 99);
		boolean headicon = false;
		if ((value & RETRIBUTION) != 0) {
			player.setHeadIcon(3);
			headicon = true;
		}
		if ((value & SMITE) != 0) {
			player.setHeadIcon(4);
			headicon = true;
		}
		if ((value & REDEMPTION) != 0) {
			player.setHeadIcon(5);
			headicon = true;
		}
		if ((value & PROT_SUMMONING) != 0) {
			player.setHeadIcon(7);
			headicon = true;
		}

		if ((value & PROT_MELEE) != 0) {
			player.setHeadIcon((value & PROT_SUMMONING) != 0 ? 8 : 0);
			headicon = true;
		}

		if ((value & PROT_RANGE) != 0) {
			player.setHeadIcon((value & PROT_SUMMONING) != 0 ? 9 : 1);
			headicon = true;
		}

		if ((value & PROT_MAGIC) != 0) {
			player.setHeadIcon((value & PROT_SUMMONING) != 0 ? 10 : 2);
			headicon = true;
		}

		if ((value & PROT_RANGE) != 0 && (value & PROT_MAGIC) != 0) {
			player.setHeadIcon(6);
			headicon = true;
		}
		if (!headicon)
			player.setHeadIcon(-1);
		// TODO: DRAIN
	}
}
