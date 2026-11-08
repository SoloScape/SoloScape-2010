package net.scapeemulator.game.msg;

import io.netty.buffer.*;
import net.scapeemulator.api.crypto.StreamCipher;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.codec.impl.IncomingMessageDecoder;
import net.scapeemulator.api.message.codec.impl.PacketDecoder;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.game.command.CommandDispatcher;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.hud.DisplayMode;
import net.scapeemulator.game.model.hud.util.WorldMap;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.NpcOptionTwoMessage;
import net.scapeemulator.game.msg.interf.WorldMapClickMessage;
import net.scapeemulator.game.msg.util.PingMessage;
import net.scapeemulator.game.util.LandscapeKeyTable;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class NpcAndWorldMapPacketTest {
	@Test
	public void clientWireFormatsDecodeAndPreserveFollowingPing() throws Exception {
		CodecRepository codecs = new CodecRepository();
		Method init = Messages.class.getDeclaredMethod("init", CodecRepository.class, LandscapeKeyTable.class);
		init.setAccessible(true);
		init.invoke(null, codecs, null);
		for (boolean running : new boolean[] { false, true }) {
			ByteBuf input = Unpooled.buffer();
			MessageBuf<Object> packets = Unpooled.messageBuffer(), messages = Unpooled.messageBuffer();
			try {
				// Mirror the client's byte-subtract flag and unsigned big-endian index.
				input.writeByte(35).writeByte(running ? 127 : 128).writeShort(0xFEDC);
				int packed = new Position(12345, 6789, 3).toPackedInt();
				input.writeByte(57).writeByte(packed >>> 8).writeByte(packed)
						.writeByte(packed >>> 24).writeByte(packed >>> 16);
				input.writeByte(74);
				PacketDecoder framing = new PacketDecoder(new StreamCipher() {
					@Override public int nextInt() { return 0; }
				}, Messages.SIZES);
				for (int i = 0; i < 3; i++) framing.decode(null, input, packets);
				assertEquals(0, input.readableBytes());
				IncomingMessageDecoder decoder = new IncomingMessageDecoder(codecs);
				while (!packets.isEmpty()) {
					Packet packet = (Packet) packets.poll();
					try {
						decoder.decode(null, packet, messages);
						assertEquals(0, packet.getPayload().readableBytes());
					} finally { packet.getPayload().release(); }
				}
				assertEquals(3, messages.size());
				NpcOptionTwoMessage npc = (NpcOptionTwoMessage) messages.poll();
				assertEquals(0xFEDC, npc.getNpcIndex());
				assertEquals(running, npc.isRunning());
				WorldMapClickMessage map = (WorldMapClickMessage) messages.poll();
				assertTrue(map.isValid());
				assertEquals(new Position(12345, 6789, 3), map.getPosition());
				assertTrue(messages.poll() instanceof PingMessage);
			} finally { input.release(); packets.release(); messages.release(); }
		}
	}

	@Test
	public void registeredWorldMapHandlerUpdatesCenterButPreservesPlayerMarkerAndPosition() throws Exception {
		Player player = new Player();
		Position position = new Position(3220, 3440);
		player.setPosition(position);
		player.getDisplay().initialize(DisplayMode.FIXED);
		assertTrue(player.getDisplay().openRootInterface(WorldMap.INSTANCE, false));
		MessageDispatcher<Player> dispatcher = dispatcher();
		Position selected = new Position(12345, 6789, 2);
		dispatcher.dispatch(player, new WorldMapClickMessage(selected.toPackedInt()));
		assertEquals(selected.toPackedInt(), player.getStatus().getVarC(WorldMap.CENTER_POSITION_VAR));
		assertEquals(position.toPackedInt(), player.getStatus().getVarC(WorldMap.PLAYER_POSITION_VAR));
		assertEquals(position, player.getPosition());
		assertNotNull(player.getDisplay().getInterface(WorldMap.INSTANCE.getId()));
		for (int invalid : new int[] { -1, 0x40000000, 0x80000000 })
			dispatcher.dispatch(player, new WorldMapClickMessage(invalid));
		assertEquals(selected.toPackedInt(), player.getStatus().getVarC(WorldMap.CENTER_POSITION_VAR));
		player.getDisplay().close(WorldMap.INSTANCE);
		dispatcher.dispatch(player, new WorldMapClickMessage(0));
		assertEquals(selected.toPackedInt(), player.getStatus().getVarC(WorldMap.CENTER_POSITION_VAR));
	}

	static MessageDispatcher<Player> dispatcher() throws Exception {
		MessageDispatcher<Player> dispatcher = new MessageDispatcher<>();
		Method init = Messages.class.getDeclaredMethod("init", MessageDispatcher.class, CommandDispatcher.class);
		init.setAccessible(true);
		init.invoke(null, dispatcher, null);
		return dispatcher;
	}
}
