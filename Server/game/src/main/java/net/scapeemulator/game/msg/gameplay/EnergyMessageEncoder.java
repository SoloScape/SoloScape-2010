package net.scapeemulator.game.msg.gameplay;

import io.netty.buffer.ByteBufAllocator;
import net.scapeemulator.api.message.codec.MessageEncoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketBuilder;

import java.io.IOException;

public final class EnergyMessageEncoder extends MessageEncoder<EnergyMessage> {

	public EnergyMessageEncoder() {
		super(EnergyMessage.class);
	}

	@Override
	public Packet encode(ByteBufAllocator alloc, EnergyMessage message) throws IOException {
		PacketBuilder builder = new PacketBuilder(alloc, 18);
		builder.put(DataType.BYTE, message.getEnergy());
		return builder.toPacket();
	}

}
