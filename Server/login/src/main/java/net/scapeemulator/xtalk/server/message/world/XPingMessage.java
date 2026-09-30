package net.scapeemulator.xtalk.server.message.world;

import java.io.IOException;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.message.handler.MessageHandler;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.xtalk.net.gameserver.GameServerSession;

public class XPingMessage implements Message {
	private static final XPingMessage PING = new XPingMessage();
	public static final int OPCODE = 0;

	public static class Encoder extends MessageEncoder<XPingMessage> {

		public Encoder() {
			super(XPingMessage.class);
		}

		@Override
		public Packet encode(ByteBufAllocator alloc, XPingMessage message) throws IOException {
			return new PacketBuilder(alloc, OPCODE).toPacket();
		}
	}

	public static class Decoder extends MessageDecoder<XPingMessage> {

		public Decoder() {
			super(OPCODE);
		}

		@Override
		public XPingMessage decode(Packet packet) throws IOException {
			return PING;
		}
	}

	public static class Handler extends MessageHandler<GameServerSession, XPingMessage> {

		@Override
		public void handle(GameServerSession target, XPingMessage message) {
			
		}
	}
}
