package net.scapeemulator.game.io;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;

/** First-time Unstable Foundations spawn and initial facing direction. */
public final class NewCharacterSpawn {
    public static final Position POSITION = new Position(3678, 4937, 0);
    private static final Position NORTH = new Position(3678, 4938, 0);

    private NewCharacterSpawn() {
    }

    public static void initialize(Player player) {
        player.setPosition(POSITION);
        player.orientateToPosition(NORTH);
    }

    /** Preserve saved coordinates when an unfinished character reconnects. */
    public static void restorePendingFacing(Player player) {
        if (player.isCharacterCreationRequired() && POSITION.equals(player.getPosition()))
            player.orientateToPosition(NORTH);
    }
}
