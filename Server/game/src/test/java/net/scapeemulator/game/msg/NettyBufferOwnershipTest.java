package net.scapeemulator.game.msg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedByteChannel;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.codec.impl.IncomingMessageDecoder;
import net.scapeemulator.api.message.codec.impl.PacketEncoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.net.login.LoginEncoder;
import net.scapeemulator.game.net.login.LoginResponse;
import net.scapeemulator.js5.FileResponse;
import net.scapeemulator.js5.FileResponseEncoder;
import org.junit.Test;

import java.lang.reflect.Field;
import static org.junit.Assert.*;

public class NettyBufferOwnershipTest {

    @Test
    public void outboundPacketPayloadIsReleasedAfterWriting() throws Exception {
        ByteBuf payload = Unpooled.buffer().writeInt(0x12345678);
        ByteBuf encoded = Unpooled.buffer();
        try {
            Packet packet = new Packet(42, Packet.Type.FIXED, payload);
            new PacketEncoder(() -> 0).encode(null, packet, encoded);
            assertEquals(0, payload.refCnt());
            assertEquals(42, encoded.readUnsignedByte());
            assertEquals(0x12345678, encoded.readInt());
        } finally {
            if (payload.refCnt() > 0) payload.release();
            encoded.release();
        }
    }

    @Test
    public void packetIsReleasedEvenWhenNoIncomingDecoderExists() throws Exception {
        ByteBuf payload = Unpooled.buffer().writeByte(0x42);
        MessageBuf<Object> output = Unpooled.messageBuffer();
        Packet packet = new Packet(255, Packet.Type.FIXED, payload);
        new IncomingMessageDecoder(new CodecRepository()).decode(null, packet, output);
        assertEquals(0, payload.refCnt());
        assertTrue(output.isEmpty());
    }

    @Test
    public void consumedRawBuilderReleasesItsTemporaryBuffer() throws Exception {
        PacketBuilder parent = new PacketBuilder(Unpooled.buffer().alloc(), 88);
        PacketBuilder child = new PacketBuilder(Unpooled.buffer().alloc());
        child.putBytes(new byte[]{5, 6, 7});
        Field field = PacketBuilder.class.getDeclaredField("buffer");
        field.setAccessible(true);
        ByteBuf childBuffer = (ByteBuf) field.get(child);
        Packet packet = null;
        try {
            parent.putRawBuilder(child);
            assertEquals(0, childBuffer.refCnt());
            packet = parent.toPacket();
            assertEquals(3, packet.getPayload().readableBytes());
        } finally {
            if (childBuffer.refCnt() > 0) childBuffer.release();
            if (packet != null) packet.getPayload().release();
            else ((ByteBuf) field.get(parent)).release();
        }
    }

    @Test
    public void keyExchangeResponseReleasesItsBuffer() {
        ByteBuf payload = Unpooled.buffer().writeLong(0x123456789ABCDEFL);
        ByteBuf encoded = Unpooled.buffer();
        try {
            new LoginEncoder().encode(null, new LoginResponse(LoginResponse.STATUS_EXCHANGE_KEYS, payload), encoded);
            assertEquals(0, payload.refCnt());
            assertEquals(0, encoded.readUnsignedByte());
            assertEquals(0x123456789ABCDEFL, encoded.readLong());
        } finally {
            if (payload.refCnt() > 0) payload.release();
            encoded.release();
        }
    }

    @Test
    public void loginFailureWithEmptySharedPayloadStillEncodes() {
        EmbeddedByteChannel channel = new EmbeddedByteChannel(new LoginEncoder());
        channel.write(new LoginResponse(LoginResponse.STATUS_INVALID_PASSWORD));
        ByteBuf encoded = channel.readOutbound();
        try {
            assertEquals(LoginResponse.STATUS_INVALID_PASSWORD, encoded.readUnsignedByte());
            assertEquals(0, encoded.readableBytes());
        } finally {
            encoded.release();
        }
    }

    @Test
    public void js5ResponseReleasesContainerWithoutCreatingExtraChunks() throws Exception {
        ByteBuf container = Unpooled.buffer().writeByte(0);
        for (int i = 0; i < 600; i++) container.writeByte(i);
        ByteBuf encoded = Unpooled.buffer();
        try {
            new FileResponseEncoder().encode(null, new FileResponse(true, 255, 1, container), encoded);
            assertEquals(0, container.refCnt());
            assertEquals(255, encoded.readUnsignedByte());
            assertEquals(1, encoded.readUnsignedShort());
            assertEquals(0, encoded.readUnsignedByte());
            assertEquals(0xFF, encoded.getUnsignedByte(4 + 508));
        } finally {
            if (container.refCnt() > 0) container.release();
            encoded.release();
        }
    }
}
