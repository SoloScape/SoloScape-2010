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
import net.scapeemulator.game.msg.interf.child.InterfaceVisibleMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** One-at-a-time tab visibility/unlocks for Unstable Foundations.
 *  Lesson handlers call unlockTab at the relevant tutorial checkpoint.
 */
public final class TutorialTabs {
    private static final Logger logger = LoggerFactory.getLogger(TutorialTabs.class);

    private TutorialTabs() {
    }

    public static void refreshIcons(Player player, int rootId) {
        if (!player.isTutorialTabsRestricted())
            return;
        for (int slot = 0; slot < 16; slot++) {
            int child = iconChild(rootId, slot);
            if (child >= 0)
                player.send(new InterfaceVisibleMessage(rootId, child, player.isTutorialTabUnlocked(slot)));
        }
    }

    /** Sprite/icon widget per tab in the original build-592 548/746 HUD cache. */
    public static int iconChild(int rootId, int slot) {
        if (slot < 0 || slot >= 16)
            return -1;
        if (rootId == 548)
            return slot < 8 ? 59 + slot : 89 + slot - 8;
        if (rootId == 746)
            return 123 - slot;
        return -1;
    }

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
        if (player.getDisplay().getMode() != null)
            refreshIcons(player, player.getDisplay().getMode().getRoot().getId());
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
        // Reset the per-component hidden state when lifting the restriction.
        for (int slot = 0; slot < 16; slot++) {
            int fixed = iconChild(548, slot);
            int resized = iconChild(746, slot);
            player.send(new InterfaceVisibleMessage(548, fixed, true));
            player.send(new InterfaceVisibleMessage(746, resized, true));
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
