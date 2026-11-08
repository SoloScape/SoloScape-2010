package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.tools.ControlsInterfaceInstaller;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.anticheat.CameraMessage;
import net.scapeemulator.game.msg.anticheat.CameraMessageHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public final class ControlsTutorialTest {
    @Test
    public void overlayAppearsOnlyForConfirmedCharactersWithPendingControlsLesson() {
        Player player = new Player();
        player.getDisplay().initialize(DisplayMode.FIXED);
        ControlsTutorial.openIfPending(player);
        assertNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));

        player.setControlsTutorialPending(true);
        player.setCharacterCreationRequired(true);
        ControlsTutorial.openIfPending(player);
        assertNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));

        player.setCharacterCreationRequired(false);
        ControlsTutorial.openIfPending(player);
        assertSame(ControlsTutorial.INSTANCE, player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        ControlsTutorial.openIfPending(player);
        assertSame(ControlsTutorial.INSTANCE, player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        assertEquals("Controls", ControlsTutorial.TITLE);
        assertTrue(ControlsTutorial.INSTRUCTIONS.contains("Arrow keys rotate the camera."));
        assertTrue(ControlsTutorial.INSTRUCTIONS.contains("ground or minimap to walk."));
        assertTrue(ControlsTutorial.INSTRUCTIONS.contains("click to run."));
        assertTrue(ControlsTutorial.INSTRUCTIONS.contains("Please rotate the camera to begin."));
    }

    @Test
    public void cameraAngleChangeDismissesOverlayButIdenticalAnglesDoNot() {
        Player player = new Player();
        player.getDisplay().initialize(DisplayMode.FIXED);
        player.setControlsTutorialPending(true);

        // Camera synchronisation alone is not evidence of user rotation.
        CameraMessageHandler camera = new CameraMessageHandler();
        camera.handle(player, new CameraMessage(100, 200));
        assertTrue(player.isControlsTutorialPending());

        ControlsTutorial.openIfPending(player);
        camera.handle(player, new CameraMessage(100, 200));
        assertNotNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        assertTrue(player.isControlsTutorialPending());

        camera.handle(player, new CameraMessage(101, 200));
        // A camera change just after opening may be automatic HUD reset.
        assertNotNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        player.recordControlsTutorialShown(System.currentTimeMillis() - 2000);
        camera.handle(player, new CameraMessage(102, 200)); // Fresh baseline.
        assertNotNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        camera.handle(player, new CameraMessage(103, 200)); // Subsequent rotation.
        assertNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        assertFalse(player.isControlsTutorialPending());

        camera.handle(player, new CameraMessage(102, 200));
        ControlsTutorial.openIfPending(player);
        assertNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
    }

    @Test
    public void transitioningFromCharacterSetupDefersTheControlsWindowUntilHudIsReady() {
        Player player = new Player();
        player.getDisplay().initialize(DisplayMode.RESIZED);
        player.setCharacterCreationRequired(false);
        player.setControlsTutorialPending(true);
        player.deferControlsTutorial(2);
        ControlsTutorial.openIfPending(player);
        assertNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        ControlsTutorial.openIfPending(player);
        assertNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        ControlsTutorial.openIfPending(player);
        assertNotNull(player.getDisplay().getInterface(ControlsInterfaceInstaller.INTERFACE_ID));
        assertEquals(DisplayMode.RESIZED.getCenterRoot(),
                ControlsTutorial.INSTANCE.getDisplaySlot(DisplayMode.RESIZED));
        assertTrue(player.isControlsTutorialPending());
    }

    @Test
    public void newlyCachedPopupCanBeResentButNeverFloodsTheClient() {
        Player player = new Player();
        player.setControlsTutorialPending(true);
        player.recordControlsTutorialShown(1000);
        assertFalse(player.shouldResendControlsTutorial(5999));
        assertTrue(player.shouldResendControlsTutorial(6000));
        assertFalse(player.shouldResendControlsTutorial(6001));
        assertTrue(player.shouldResendControlsTutorial(11000));
        assertTrue(player.shouldResendControlsTutorial(16000));
        assertFalse(player.shouldResendControlsTutorial(22000));
    }

    @Test
    public void cameraUpdatesDoNotCompleteControlsLessonBeforeOverlayAppears() {
        Player player = new Player();
        player.setControlsTutorialPending(true);
        new CameraMessageHandler().handle(player, new CameraMessage(200, 400));
        new CameraMessageHandler().handle(player, new CameraMessage(300, 400));
        assertTrue(player.isControlsTutorialPending());
    }
}
