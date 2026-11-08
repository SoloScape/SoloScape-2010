package net.scapeemulator.game.msg;

import io.netty.buffer.Unpooled;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.msg.anticheat.MouseMovementMessageDecoder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MouseTelemetryMessageTest {
    @Test
    public void clickTelemetryPacketTwentyOneIsConsumed() throws Exception {
        PacketBuilder builder = new PacketBuilder(Unpooled.buffer().alloc(), 21, Packet.Type.VARIABLE_BYTE);
        builder.put(DataType.BYTE, 0x12);
        builder.put(DataType.BYTE, 0x34);
        builder.put(DataType.BYTE, 0x56);
        Packet packet = builder.toPacket();
        try {
            assertNotNull(new MouseMovementMessageDecoder(21).decode(packet));
            assertEquals(0, packet.getPayload().readableBytes());
        } finally {
            packet.getPayload().release();
        }
    }
}
