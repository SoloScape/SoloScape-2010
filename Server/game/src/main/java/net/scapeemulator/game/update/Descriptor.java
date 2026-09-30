package net.scapeemulator.game.update;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.net.packet.PacketBuilder;

public abstract class Descriptor<T extends Message> {
	public abstract void encode(T message, PacketBuilder builder, PacketBuilder blockBuilder);
}
