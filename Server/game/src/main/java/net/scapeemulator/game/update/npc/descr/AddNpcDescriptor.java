package net.scapeemulator.game.update.npc.descr;

import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.Direction;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.NpcDescriptor;

public final class AddNpcDescriptor extends NpcDescriptor {

	private final int id, type;
	private final Direction direction;
	private final Position position;

	public AddNpcDescriptor(Npc npc) {
		super(npc);
		this.id = npc.getId();
		this.type = npc.getType();
		this.direction = npc.getMostRecentDirection();
		this.position = npc.getPosition();
	}

	@Override
	public void encodeDescriptor(NpcUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		int x = position.getX() - message.getPosition().getX();
		int y = position.getY() - message.getPosition().getY();

		builder.putBits(15, id);
		builder.putBits(1, 0); // check
		builder.putBits(2, 0); // check
		builder.putBits(1, isBlockUpdatedRequired() ? 1 : 0);
		builder.putBits(5, y);
		builder.putBits(5, x);
		builder.putBits(3, direction.toInteger());
		builder.putBits(14, type);
	}
}