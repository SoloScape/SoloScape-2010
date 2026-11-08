package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.cache.tools.ControlsInterfaceInstaller;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.InterfaceOpenMessage;
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
        // 746:6 is drawn beneath the game scene. Open on the central
        // foreground pane (746:8 / 548:6) with artwork sized to fit 512x334.
        return mode.getCenterRoot();
    }

    public static void openIfPending(Player player) {
        if (!player.isControlsTutorialPending() || player.isCharacterCreationRequired())
            return;
        if (player.getDisplay().getInterface(INSTANCE.getId()) != null) {
            // A client may silently skip a new interface while its JS5 group
            // is downloading. A bounded re-open helps recover without asking
            // the player to remake their character or reconnect.
            if (player.shouldResendControlsTutorial(System.currentTimeMillis())) {
                DisplayMode mode = player.getDisplay().getMode();
                int pointer = (mode.getRoot().getId() << 16) | INSTANCE.getDisplaySlot(mode);
                player.send(new InterfaceOpenMessage(pointer, INSTANCE));
                logger.info("Reasserted Controls interface {} for {} on pointer {}.",
                        INSTANCE.getId(), player.getUsername(), pointer);
            }
            return;
        }
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
