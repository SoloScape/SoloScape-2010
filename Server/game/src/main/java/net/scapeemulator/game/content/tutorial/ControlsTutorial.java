package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.tools.ControlsInterfaceInstaller;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The first Unstable Foundations controls lesson, displayed in a centred
 * window backed by a dedicated IF3 sprite (not interface 371, which is the
 * flashing Tutorial Island Progress panel).
 */
public final class ControlsTutorial extends Interface {
    public static final ControlsTutorial INSTANCE = new ControlsTutorial();
    private static final Logger logger = LoggerFactory.getLogger(ControlsTutorial.class);

    public static final String TITLE = "Controls";
    public static final String INSTRUCTIONS =
            "Arrow keys rotate the camera. Click on the ground or minimap to walk. "
            + "CTRL + click to run. Please rotate the camera to begin.";

    private ControlsTutorial() {
        super(ControlsInterfaceInstaller.INTERFACE_ID, InterfacePersistence.PERSISTENT);
    }

    @Override
    public int getDisplaySlot(DisplayMode mode) {
        // 746:8 is a clipped 512x334 scene widget, shorter than the 410px
        // artwork. In resizable mode use 746:6, the fullscreen overlay pane.
        // Fixed mode has a 512x334 game view, so use its centre pane.
        return mode == DisplayMode.RESIZED || mode == DisplayMode.FULLSCREEN
                ? mode.getPaneRoot() : mode.getCenterRoot();
    }

    public static void openIfPending(Player player) {
        if (!player.isControlsTutorialPending() || player.isCharacterCreationRequired()
                || player.getDisplay().getInterface(INSTANCE.getId()) != null)
            return;
        if (player.isControlsTutorialOpenDeferred())
            return;
        if (player.getDisplay().open(INSTANCE, true)) {
            player.recordControlsTutorialShown(System.currentTimeMillis());
            logger.info("Opened centred Controls tutorial interface {} for {}.",
                    INSTANCE.getId(), player.getUsername());
        } else {
            logger.warn("Could not open Controls tutorial interface for {}.", player.getUsername());
        }
    }

    public static void completeOnCameraRotation(Player player) {
        if (!player.isControlsTutorialPending()
                || player.getDisplay().getInterface(INSTANCE.getId()) == null
                || !player.canDismissControlsTutorial(System.currentTimeMillis()))
            return;
        // Only save after the popup has been displayed long enough to be
        // readable. This filters automatic camera updates during HUD reset.
        player.setControlsTutorialPending(false);
        try {
            if (player.getSession() != null)
                player.getSession().getService().getLoginService().savePlayer(player);
        } catch (RuntimeException ex) {
            player.setControlsTutorialPending(true);
            logger.error("Could not save controls tutorial completion for {}.", player.getUsername(), ex);
            return;
        }
        player.getDisplay().close(INSTANCE);
    }
}
