package net.scapeemulator.game.update.player.descr.external;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.player.PlayerDescriptor;

public final class AddPlayerDescriptor extends PlayerDescriptor {
	private final Position position;
	private ExternalDescriptor externalDescriptor;

	public AddPlayerDescriptor(Player player, int[] tickets) {
		super(player, tickets);
		this.position = player.getPosition();
	}

	public AddPlayerDescriptor(Player player, int[] tickets, ExternalDescriptor externalDescriptor) {
		this(player, tickets);
		this.externalDescriptor = externalDescriptor;
	}

	@Override
	public void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		builder.putBit(true);// Update
		builder.putBits(2, 0);// Opcode
		builder.putBit(externalDescriptor != null);// External descriptor
		if (externalDescriptor != null) {
			externalDescriptor.encodeDescriptor(message, builder, blockBuilder);
		}
		int x = position.getX() - (position.getAreaX() << 6);
		int y = position.getY() - (position.getAreaY() << 6);
		builder.putBits(6, x);// xLocal
		builder.putBits(6, y);// yLocal
		builder.putBit(isBlockUpdatedRequired()); // check
	}
}
