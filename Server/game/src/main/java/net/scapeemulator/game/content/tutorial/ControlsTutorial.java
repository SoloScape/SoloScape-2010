package net.scapeemulator.game.content.tutorial;

import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.interf.Interface;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;
import net.scapeemulator.game.model.hud.interf.type.InterfacePersistence;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.child.InterfaceTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Original build-592 tutorial panel (interface 371), adapted as the first
 * Unstable Foundations controls lesson. No client cache editing required.
 */
public final class ControlsTutorial extends Interface {
    public static final ControlsTutorial INSTANCE = new ControlsTutorial();
    private static final Logger logger = LoggerFactory.getLogger(ControlsTutorial.class);

    public static final String TITLE = "Controls";
    public static final String INSTRUCTIONS =
            "<col=ff981f>[^] [L] [v] [R]</col> Arrow keys rotate the camera.<br>" +
            "<col=ff981f>[MOUSE]</col> Click on the ground or minimap to walk.<br>" +
            "<col=ff981f>[CTRL]</col> + click to run.<br>" +
            "<col=ffff00>Please rotate the camera to begin.</col>";

    private ControlsTutorial() {
        super(371, InterfacePersistence.PERSISTENT);
    }

    @Override
    public int getDisplaySlot(DisplayMode mode) {
        // Draw over the chatbox rather than hiding the world or capturing keys.
        return mode.getOverChatboxRoot();
    }

    public static void openIfPending(Player player) {
        if (!player.isControlsTutorialPending() || player.isCharacterCreationRequired()
                || player.getDisplay().getInterface(INSTANCE.getId()) != null)
            return;
        player.getDisplay().open(INSTANCE, true);
    }

    @Override
    public void dispatch(Player player, InterfaceAction action) {
        if (action.getType() == InterfaceAction.ActionType.OPEN_INTER) {
            // The cached tutorial panel provides its own frame and model icon.
            // Coloured keycap symbols serve as arrow/mouse/Ctrl illustrations.
            player.send(new InterfaceTextMessage(getId(), 1, "Keys"));
            player.send(new InterfaceTextMessage(getId(), 2, TITLE));
            player.send(new InterfaceTextMessage(getId(), 27, INSTRUCTIONS));
        }
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
