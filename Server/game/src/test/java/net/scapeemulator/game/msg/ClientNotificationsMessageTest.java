package net.scapeemulator.game.msg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.codec.impl.IncomingMessageDecoder;
import net.scapeemulator.api.message.codec.impl.PacketDecoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.game.msg.util.PingMessage;
import net.scapeemulator.game.msg.util.ClientPreferencesMessage;
import net.scapeemulator.game.msg.area.AreaChangeMessage;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ClientNotificationsMessageTest {
	@Test
	public void notificationsAreRegisteredAndPreserveFollowingPing() throws Exception {
		CodecRepository codecs = new CodecRepository();
		Method init = Messages.class.getDeclaredMethod("init", CodecRepository.class, LandscapeKeyTable.class);
		init.setAccessible(true);
		init.invoke(null, codecs, null);
		assertNotNull(codecs.get(17));
		assertNotNull(codecs.get(32));

		ByteBuf input = Unpooled.buffer();
		MessageBuf<Object> packets = Unpooled.messageBuffer();
		MessageBuf<Object> messages = Unpooled.messageBuffer();
		try {
			input.writeByte(17);
			input.writeByte(32).writeByte(4).writeInt(0x11010203);
			input.writeByte(74);
			PacketDecoder framing = new PacketDecoder(new StreamCipher() {
				@Override
				public int nextInt() {
					return 0;
				}
			}, Messages.SIZES);
			framing.decode(null, input, packets);
			framing.decode(null, input, packets);
			framing.decode(null, input, packets);
			assertEquals(0, input.readableBytes());
			assertEquals(3, packets.size());

			IncomingMessageDecoder decoder = new IncomingMessageDecoder(codecs);
			while (!packets.isEmpty()) {
				Packet packet = (Packet) packets.poll();
				boolean hasAllocatedPayload = packet.getPayload().readableBytes() > 0;
				decoder.decode(null, packet, messages);
				if (hasAllocatedPayload) assertEquals(0, packet.getPayload().refCnt());
			}
			assertEquals(3, messages.size());
			assertTrue(messages.poll() instanceof AreaChangeMessage);
			assertTrue(messages.poll() instanceof ClientPreferencesMessage);
			assertTrue(messages.poll() instanceof PingMessage);
		} finally {
			input.release();
			packets.release();
			messages.release();
		}
	}
}
