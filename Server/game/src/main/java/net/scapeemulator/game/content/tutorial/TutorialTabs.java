package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.game.model.hud.AdditionalTabs;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.InventoryTab;
import net.scapeemulator.game.model.hud.combat.AttackTab;
import net.scapeemulator.game.model.hud.combat.EquipmentTab;
import net.scapeemulator.game.model.hud.combat.PrayerTab;
import net.scapeemulator.game.model.hud.settings.LogoutTab;
import net.scapeemulator.game.model.hud.settings.SettingsTab;
import net.scapeemulator.game.model.hud.skill.SkillsTab;
import net.scapeemulator.game.model.hud.util.MusicTab;
import net.scapeemulator.game.model.hud.util.ObjectiveTab;
import net.scapeemulator.game.model.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** One-at-a-time tab visibility/unlocks for Unstable Foundations.
 *  Lesson handlers call unlockTab at the relevant tutorial checkpoint.
 */
public final class TutorialTabs {
    private static final Logger logger = LoggerFactory.getLogger(TutorialTabs.class);

    private TutorialTabs() {
    }

    // Tab content remains locked server-side. The client's tab icons cannot
    // safely be hidden until the real build-592 IF_SETHIDE packet is verified.
    // Opcode 21 is a SIX-BYTE jingle; sending 7-byte visibility payloads under
    // it corrupts the stream and disconnects newly created accounts.

    private static Interface interfaceFor(int slot) {
        switch (slot) {
        case Tab.ATTACK: return AttackTab.INSTANCE;
        case Tab.SKILLS: return SkillsTab.INSTANCE;
        case Tab.INVENTORY: return InventoryTab.INSTANCE;
        case Tab.EQUIPMENT: return EquipmentTab.INSTANCE;
        case Tab.PRAYER: return PrayerTab.INSTANCE;
        case Tab.OBJECTIVE: return ObjectiveTab.INSTANCE;
        case Tab.SETTINGS: return SettingsTab.INSTANCE;
        case Tab.MUSIC: return MusicTab.INSTANCE;
        case Tab.LOGOUT: return LogoutTab.INSTANCE;
        default: return AdditionalTabs.forSlot(slot);
        }
    }

    /** Reopen exactly the tabs earned so far when a character logs in. */
    public static void restoreUnlockedTabs(Player player) {
        for (int slot = 0; slot < Tab.TAB_COUNT; slot++) {
            if (player.isTutorialTabUnlocked(slot)) {
                Interface inter = interfaceFor(slot);
                if (inter != null)
                    player.getDisplay().open(inter, false);
            }
        }
    }

    /**
     * Grant exactly one additional tab and persist before revealing it.
     * Does nothing for an already-unlocked slot or a completed tutorial.
     */
    public static boolean unlockTab(Player player, int slot) {
        if (!player.isTutorialTabsRestricted() || slot < 0 || slot >= Tab.TAB_COUNT
                || player.isTutorialTabUnlocked(slot) || interfaceFor(slot) == null)
            return false;
        int oldMask = player.getTutorialTabMask();
        player.setTutorialTabState(true, oldMask | (1 << slot));
        try {
            save(player);
        } catch (RuntimeException ex) {
            player.setTutorialTabState(true, oldMask);
            logger.error("Could not persist tab {} unlock for {}.", slot, player.getUsername(), ex);
            return false;
        }
        player.getDisplay().open(interfaceFor(slot), false);
        return true;
    }

    /** Restore the normal HUD at the end of the whole tutorial. */
    public static void unlockAll(Player player) {
        if (!player.isTutorialTabsRestricted())
            return;
        int oldMask = player.getTutorialTabMask();
        player.setTutorialTabState(false, (1 << Tab.TAB_COUNT) - 1);
        try {
            save(player);
        } catch (RuntimeException ex) {
            player.setTutorialTabState(true, oldMask);
            logger.error("Could not persist tutorial tab completion for {}.", player.getUsername(), ex);
            return;
        }
        // Interface instances are populated during online game initialization.
        // Offline recovery can update the saved mask without opening HUD tabs.
        if (player.getSession() == null)
            return;
        for (int slot = 0; slot < Tab.TAB_COUNT; slot++) {
            Interface inter = interfaceFor(slot);
            if (inter != null) player.getDisplay().open(inter, false);
        }
        player.getDisplay().open(LogoutTab.INSTANCE, false);
    }

    private static void save(Player player) {
        if (player.getSession() != null)
            player.getSession().getService().getLoginService().savePlayer(player);
    }
}
