package net.scapeemulator.game.msg;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.codec.impl.IncomingMessageDecoder;
import net.scapeemulator.api.message.codec.impl.PacketDecoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.settings.SettingsTab;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.anticheat.MouseMovementMessage;
import net.scapeemulator.game.msg.anticheat.MouseMovementMessageHandler;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MouseMovementMessageTest {
	@Test
	public void telemetryIsRegisteredAndDoesNotConsumeFollowingSettingsClick() throws Exception {
		CodecRepository codecs = new CodecRepository();
		Method init = Messages.class.getDeclaredMethod("init", CodecRepository.class, LandscapeKeyTable.class);
		init.setAccessible(true);
		init.invoke(null, codecs, null);
		assertNotNull(codecs.get(42));

		ByteBuf input = Unpooled.buffer();
		MessageBuf<Object> packets = Unpooled.messageBuffer();
		MessageBuf<Object> messages = Unpooled.messageBuffer();
		try {
			input.writeByte(42).writeByte(5).writeBytes(new byte[] { 1, 2, 3, 4, 5 });
			input.writeByte(18).writeInt((261 << 16) | 3).writeShort(-1);
			PacketDecoder framing = new PacketDecoder(new StreamCipher() {
				@Override
				public int nextInt() {
					return 0;
				}
			}, Messages.SIZES);
			framing.decode(null, input, packets);
			framing.decode(null, input, packets);
			assertEquals(0, input.readableBytes());
			assertEquals(2, packets.size());

			IncomingMessageDecoder decoder = new IncomingMessageDecoder(codecs);
			while (!packets.isEmpty()) {
				Packet packet = (Packet) packets.poll();
				decoder.decode(null, packet, messages);
				assertEquals(0, packet.getPayload().refCnt());
			}
			assertEquals(2, messages.size());
			Player player = new Player();
			player.setPosition(new Position(3200, 3200));
			Object telemetry = messages.poll();
			assertTrue(telemetry instanceof MouseMovementMessage);
			new MouseMovementMessageHandler().handle(player, (MouseMovementMessage) telemetry);
			assertFalse(player.isRunning());
			assertEquals(new Position(3200, 3200), player.getPosition());
			assertEquals(100, player.getEnergy());
			ChildInterfaceClickAction click = (ChildInterfaceClickAction) messages.poll();
			SettingsTab.INSTANCE.dispatch(player, click);
			assertTrue(player.isRunning());
		} finally {
			input.release();
			packets.release();
			messages.release();
		}
	}
}
