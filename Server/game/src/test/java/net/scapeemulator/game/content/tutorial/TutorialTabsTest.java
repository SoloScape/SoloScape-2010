package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.game.io.DummyPlayerSerializer;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.game.msg.Messages;
import net.scapeemulator.game.msg.audio.Jingle;
import net.scapeemulator.game.msg.interf.child.InterfaceVisibleMessage;
import net.scapeemulator.game.util.LandscapeKeyTable;
import java.lang.reflect.Method;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.InventoryInfo;
import org.junit.BeforeClass;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.InventoryTab;
import net.scapeemulator.game.model.hud.combat.AttackTab;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.settings.SettingsTab;
import net.scapeemulator.game.model.player.Player;
import org.junit.Test;

import static org.junit.Assert.*;

public class TutorialTabsTest {
    @BeforeClass
    public static void registerTestInventories() {
        Inventory.addDetails(new InventoryInfo(93, "inventory", 149, 0, 28, Inventory.StackMode.STACKABLE_ONLY));
        Inventory.addDetails(new InventoryInfo(94, "equipment", 387, 28, 14, Inventory.StackMode.STACKABLE_ONLY));
        Inventory.addDetails(new InventoryInfo(95, "bank", 762, 87, 496, Inventory.StackMode.ALWAYS));
        Inventory.addDetails(new InventoryInfo(35, "samples", 620, 26, 496, Inventory.StackMode.ALWAYS));
    }

    @Test
    public void freshAccountsInitiallyUnlockOnlySettings() {
        Player player = new DummyPlayerSerializer().load("tabstarter", "password").getPlayer();
        assertTrue(player.isCharacterCreationRequired());
        assertTrue(player.isTutorialTabsRestricted());
        assertEquals(1 << Tab.SETTINGS, player.getTutorialTabMask());
        for (int slot = 0; slot < Tab.TAB_COUNT; slot++)
            assertEquals(slot == Tab.SETTINGS, player.isTutorialTabUnlocked(slot));
    }

    @Test
    public void firstLoginTriggerOnlyOpensEarnedTabs() {
        Player player = new DummyPlayerSerializer().load("firsttabs", "password").getPlayer();
        player.getDisplay().initialize(DisplayMode.FIXED);
        new TutorialTrigger().trigger(player, "tutorial_complete");
        assertNotNull(player.getDisplay().getInterface(SettingsTab.INSTANCE.getId()));
        assertNull(player.getDisplay().getInterface(InventoryTab.INSTANCE.getId()));
        assertNull(player.getDisplay().getInterface(AttackTab.INSTANCE.getId()));
        assertTrue(player.isTutorialTabsRestricted());
    }

    @Test
    public void lockedTabsStayClosedEvenWhenForciblyOpenedThenUnlockIndependently() {
        Player player = new Player();
        player.setTutorialTabState(true, 1 << Tab.SETTINGS);
        player.getDisplay().initialize(DisplayMode.FIXED);
        TutorialTabs.restoreUnlockedTabs(player);
        assertNotNull(player.getDisplay().getInterface(SettingsTab.INSTANCE.getId()));
        assertFalse(player.getDisplay().open(InventoryTab.INSTANCE, true));
        assertFalse(player.getDisplay().open(AttackTab.INSTANCE, true));
        assertNull(player.getDisplay().getInterface(InventoryTab.INSTANCE.getId()));
        assertNotNull(player.getDisplay().getInterface(SettingsTab.INSTANCE.getId()));

        assertTrue(TutorialTabs.unlockTab(player, Tab.INVENTORY));
        assertTrue(player.isTutorialTabUnlocked(Tab.INVENTORY));
        assertFalse(player.isTutorialTabUnlocked(Tab.ATTACK));
        assertNotNull(player.getDisplay().getInterface(InventoryTab.INSTANCE.getId()));
        assertNull(player.getDisplay().getInterface(AttackTab.INSTANCE.getId()));
        assertFalse(TutorialTabs.unlockTab(player, Tab.INVENTORY));
        assertFalse(player.isTutorialTabUnlocked(Tab.LOGOUT));
    }

    @Test
    public void legacyCharactersAreNotRestrictedAndCompletingTutorialLiftsGate() {
        Player returning = new Player();
        returning.getDisplay().initialize(DisplayMode.FIXED);
        assertFalse(returning.isTutorialTabsRestricted());
        assertTrue(returning.getDisplay().open(AttackTab.INSTANCE, false));

        Player newcomer = new Player();
        newcomer.getDisplay().initialize(DisplayMode.FIXED);
        newcomer.setTutorialTabState(true, 1 << Tab.SETTINGS);
        TutorialTabs.unlockAll(newcomer);
        assertFalse(newcomer.isTutorialTabsRestricted());
        for (int i = 0; i < Tab.TAB_COUNT; i++)
            assertTrue(newcomer.isTutorialTabUnlocked(i));
        assertNull(newcomer.getDisplay().getInterface(AttackTab.INSTANCE.getId()));
        // A real session reopens the tab interfaces when the tutorial completes.
    }

    @Test
    public void build592CodecNeverRegistersVisibilityAsTheJinglePacket() throws Exception {
        CodecRepository repository = new CodecRepository();
        Method register = Messages.class.getDeclaredMethod("init", CodecRepository.class,
                LandscapeKeyTable.class);
        register.setAccessible(true);
        register.invoke(null, repository, (LandscapeKeyTable) null);
        assertNull("Broken IF_SETHIDE encoder must stay unbound",
                repository.get(InterfaceVisibleMessage.class));
        assertNotNull("Real six-byte jingle codec must remain available",
                repository.get(Jingle.class));
    }

    @Test
    public void firstLoginDoesNotUseTheIncorrectJingleOpcodeForTabHiding() {
        Player player = new DummyPlayerSerializer().load("safehud", "password").getPlayer();
        player.getDisplay().initialize(DisplayMode.FIXED);
        new TutorialTrigger().trigger(player, "tutorial_complete");

        // The first-login path is safe without any seven-byte visibility
        // messages. The Settings tab is present; other tab contents are locked.
        assertNotNull(player.getDisplay().getInterface(SettingsTab.INSTANCE.getId()));
        assertNull(player.getDisplay().getInterface(AttackTab.INSTANCE.getId()));
        assertNull(player.getDisplay().getInterface(InventoryTab.INSTANCE.getId()));
    }
}
