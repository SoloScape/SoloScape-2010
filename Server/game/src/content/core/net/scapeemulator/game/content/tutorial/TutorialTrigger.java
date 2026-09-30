package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.game.model.hud.Chat;
import net.scapeemulator.game.model.hud.InventoryTab;
import net.scapeemulator.game.model.hud.combat.AttackTab;
import net.scapeemulator.game.model.hud.combat.EquipmentTab;
import net.scapeemulator.game.model.hud.combat.PrayerTab;
import net.scapeemulator.game.model.hud.orb.HealthOrb;
import net.scapeemulator.game.model.hud.orb.PrayerOrb;
import net.scapeemulator.game.model.hud.orb.RunOrb;
import net.scapeemulator.game.model.hud.orb.SummoningOrb;
import net.scapeemulator.game.model.hud.settings.ChatRoot;
import net.scapeemulator.game.model.hud.settings.ChatSettings;
import net.scapeemulator.game.model.hud.settings.LogoutTab;
import net.scapeemulator.game.model.hud.settings.SettingsTab;
import net.scapeemulator.game.model.hud.skill.SkillsTab;
import net.scapeemulator.game.model.hud.util.MusicTab;
import net.scapeemulator.game.model.hud.util.ObjectiveTab;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.Trigger;

public class TutorialTrigger extends Trigger<String> {

	@Override
	public void trigger(Player player, String current) {
		openInterfaces(player, current);
		switch (current) {
		default:
			break;
		}
	}

	private void openInterfaces(Player player, String status) {
		switch (status) {
		case "tutorial_complete":
		default:
			if (status.equalsIgnoreCase("tutorial_complete")) {
				player.getDisplay().open(PrayerTab.INSTANCE, false);
				player.getDisplay().open(InventoryTab.INSTANCE, false);
				player.getDisplay().open(MusicTab.INSTANCE, false);
				player.getDisplay().open(SkillsTab.INSTANCE, false);
				player.getDisplay().open(AttackTab.INSTANCE, false);
				player.getDisplay().open(SettingsTab.INSTANCE, false);
				player.getDisplay().open(EquipmentTab.INSTANCE, false);
				player.getDisplay().open(ObjectiveTab.INSTANCE, false);
				player.getDisplay().openInterface(Chat.INSTANCE, 752, 9, false);
				// Close tutorial interfaces
				player.getDisplay().close(TutorialIslandProgress.INSTANCE);
			} else {
				// Open tutorial interfaces
				player.getDisplay().open(TutorialIslandProgress.INSTANCE, false);
			}
			player.getDisplay().open(LogoutTab.INSTANCE, false);
			player.getDisplay().open(ChatRoot.INSTANCE, false);
			player.getDisplay().open(ChatSettings.INSTANCE, false);

			player.getDisplay().open(HealthOrb.INSTANCE, false);
			player.getDisplay().open(PrayerOrb.INSTANCE, false);
			player.getDisplay().open(RunOrb.INSTANCE, false);
			// eh?
			player.getDisplay().open(SummoningOrb.INSTANCE, false);
			break;
		}
	}
}
