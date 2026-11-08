package net.scapeemulator.game.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.update.player.block.OrientateToCoordinatesPlayerBlock;
import org.junit.Test;

import static org.junit.Assert.*;

public class NewCharacterSpawnTest {
    @Test
    public void newCharacterSpawnsAtRequestedTileAndFacesNorth() {
        Player player = new Player();
        NewCharacterSpawn.initialize(player);
        assertEquals(new Position(3678, 4937, 0), player.getPosition());
        assertEquals(new Position(3678, 4938, 0), player.getOrientationPosition());

        // The initial player-update block must transmit north, rather than
        // just recording a direction that the client never receives.
        PacketBuilder builder = new PacketBuilder(Unpooled.buffer().alloc(), 1);
        Packet packet = null;
        try {
            new OrientateToCoordinatesPlayerBlock(player).encode(null, builder);
            packet = builder.toPacket();
            ByteBuf buffer = packet.getPayload();
            assertEquals(8192, buffer.readUnsignedShort());
            assertEquals(0, buffer.readableBytes());
        } finally {
            if (packet != null) packet.getPayload().release();
        }
    }

    @Test
    public void restoredUnfinishedCharacterOnlyFacesNorthAtOriginalSpawn() {
        Player pending = new Player();
        pending.setCharacterCreationRequired(true);
        pending.setPosition(NewCharacterSpawn.POSITION);
        NewCharacterSpawn.restorePendingFacing(pending);
        assertEquals(new Position(3678, 4938, 0), pending.getOrientationPosition());

        Player moved = new Player();
        moved.setCharacterCreationRequired(true);
        moved.setPosition(new Position(3679, 4937, 0));
        NewCharacterSpawn.restorePendingFacing(moved);
        assertNull(moved.getOrientationPosition());
        assertEquals(new Position(3679, 4937, 0), moved.getPosition());

        Player returning = new Player();
        returning.setPosition(NewCharacterSpawn.POSITION);
        returning.setCharacterCreationRequired(false);
        NewCharacterSpawn.restorePendingFacing(returning);
        assertNull(returning.getOrientationPosition());
    }
}
