package net.scapeemulator.xtalk.client.login;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.ByteBufUtils;
import net.scapeemulator.xtalk.ServerLoginResult;
import org.junit.Test;

import static org.junit.Assert.*;

public final class CrosstalkLoginDecoderTest {

    @Test
    public void oneByteRejectionStatusesAreCompleteResponses() {
        for (int status : new int[] {
                ServerLoginResult.KEY_MISMATCH,
                ServerLoginResult.WORLD_ALREADY_ONLINE,
                ServerLoginResult.INVALID_VERSION,
                ServerLoginResult.INVALID_WORLD }) {
            ByteBuf incoming = Unpooled.buffer().writeByte(status);
            try {
                ServerLoginResult result = CrosstalkLoginDecoder.parseLoginResponse(incoming);
                assertNotNull(result);
                assertEquals(status, result.getResult());
                assertTrue(result.getCountries().isEmpty());
                assertEquals(0, incoming.readableBytes());
            } finally {
                incoming.release();
            }
        }
    }

    @Test
    public void successWithoutCountriesCanArriveInTwoReads() {
        ByteBuf incoming = Unpooled.buffer().writeByte(ServerLoginResult.SUCCESS);
        try {
            assertNull(CrosstalkLoginDecoder.parseLoginResponse(incoming));
            assertEquals(0, incoming.readerIndex());
            incoming.writeByte(0);
            ServerLoginResult result = CrosstalkLoginDecoder.parseLoginResponse(incoming);
            assertNotNull(result);
            assertEquals(ServerLoginResult.SUCCESS, result.getResult());
            assertTrue(result.getCountries().isEmpty());
            assertEquals(2, incoming.readerIndex());
        } finally {
            incoming.release();
        }
    }

    @Test
    public void successCountriesCanBeFragmentedAtEveryByteBoundary() {
        ByteBuf expected = Unpooled.buffer();
        expected.writeByte(ServerLoginResult.SUCCESS);
        expected.writeByte(2);
        expected.writeByte(1);
        ByteBufUtils.writeString(expected, "UK");
        expected.writeByte(2);
        ByteBufUtils.writeString(expected, "Finland");
        ByteBuf incoming = Unpooled.buffer();
        try {
            int bytes = expected.readableBytes();
            for (int i = 0; i < bytes; i++) {
                incoming.writeByte(expected.getUnsignedByte(i));
                ServerLoginResult result = CrosstalkLoginDecoder.parseLoginResponse(incoming);
                if (i < bytes - 1) {
                    assertNull("Must wait at byte " + i, result);
                    assertEquals(0, incoming.readerIndex());
                } else {
                    assertNotNull(result);
                    assertEquals(ServerLoginResult.SUCCESS, result.getResult());
                    assertEquals(2, result.getCountries().size());
                    assertEquals("UK", result.getCountries().get(0).getName());
                    assertEquals("Finland", result.getCountries().get(1).getName());
                    assertEquals(incoming.writerIndex(), incoming.readerIndex());
                }
            }
        } finally {
            expected.release();
            incoming.release();
        }
    }

    @Test
    public void parsingFromNonzeroReaderIndexWorks() {
        ByteBuf incoming = Unpooled.buffer();
        try {
            incoming.writeLong(0x123456789ABCDEFL);
            incoming.writeByte(ServerLoginResult.SUCCESS);
            incoming.writeByte(1);
            incoming.writeByte(3);
            ByteBufUtils.writeString(incoming, "England");
            incoming.skipBytes(8);
            ServerLoginResult result = CrosstalkLoginDecoder.parseLoginResponse(incoming);
            assertNotNull(result);
            assertEquals("England", result.getCountries().get(0).getName());
            assertEquals(0, incoming.readableBytes());
        } finally {
            incoming.release();
        }
    }
}
