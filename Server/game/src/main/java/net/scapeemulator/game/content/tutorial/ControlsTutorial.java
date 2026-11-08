package net.scapeemulator.game.content.tutorial;

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
        // Centre over the game scene, like the original tutorial screenshot.
        // This passive sprite does not intercept the camera arrow keys.
        return mode.getCenterRoot();
    }

    public static void openIfPending(Player player) {
        if (!player.isControlsTutorialPending() || player.isCharacterCreationRequired()
                || player.getDisplay().getInterface(INSTANCE.getId()) != null)
            return;
        player.getDisplay().open(INSTANCE, true);
    }

    public static void completeOnCameraRotation(Player player) {
        if (!player.isControlsTutorialPending()
                || player.getDisplay().getInterface(INSTANCE.getId()) == null)
            return;
        // Only save once an actual camera-angle change is observed, not on open.
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
