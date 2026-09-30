package net.scapeemulator.game.update.npc;

import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.mob.Direction;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.npc.block.AnimationNpcBlock;
import net.scapeemulator.game.update.npc.block.NpcShoutBlock;
import net.scapeemulator.game.update.npc.block.PrimaryDamageNpcBlock;
import net.scapeemulator.game.update.npc.block.SecondaryDamageNpcBlock;
import net.scapeemulator.game.update.npc.block.SpotAnimationNpcBlock;
import net.scapeemulator.game.update.npc.block.TransformationNpcBlock;
import net.scapeemulator.game.update.npc.descr.IdleNpcDescriptor;
import net.scapeemulator.game.update.npc.descr.RunNpcDescriptor;
import net.scapeemulator.game.update.npc.descr.WalkNpcDescriptor;

import java.util.HashMap;
import java.util.Map;

public abstract class NpcDescriptor {

	public static NpcDescriptor create(Npc npc) {
		Direction walkDirection = Direction.NONE, runDirection = Direction.NONE;
		if (npc.getWalkStep() != null)
			walkDirection = Direction.between(npc.getStartStep(), npc.getWalkStep());
		if (npc.getRunStep() != null)
			runDirection = Direction.between(npc.getWalkStep(), npc.getRunStep());

		if (walkDirection == Direction.NONE)
			return new IdleNpcDescriptor(npc);
		else if (runDirection == Direction.NONE)
			return new WalkNpcDescriptor(npc, walkDirection);
		else
			return new RunNpcDescriptor(npc, walkDirection, runDirection);
	}

	private final Map<Class<? extends NpcBlock>, NpcBlock> blocks = new HashMap<>();

	public NpcDescriptor(Npc npc) {

		if (npc.doTransform())
			addBlock(new TransformationNpcBlock(npc));

		if (npc.isAnimationUpdated())
			addBlock(new AnimationNpcBlock(npc));

		if (npc.isSpotAnimationUpdated())
			addBlock(new SpotAnimationNpcBlock(npc));

		if (npc.getShout() != null)
			addBlock(new NpcShoutBlock(npc));

		if (npc.getPrimaryHit() != null)
			addBlock(new PrimaryDamageNpcBlock(npc));

		if (npc.getSecondaryHit() != null)
			addBlock(new SecondaryDamageNpcBlock(npc));
	}

	private void addBlock(NpcBlock block) {
		blocks.put(block.getClass(), block);
	}

	public boolean isBlockUpdatedRequired() {
		return !blocks.isEmpty();
	}

	public void encode(NpcUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		encodeDescriptor(message, builder, blockBuilder);

		if (isBlockUpdatedRequired()) {
			int flags = 0;
			for (NpcBlock block : blocks.values())
				flags |= block.getFlag();

			if (flags > 0xFF) {
				flags |= 0x40;
				blockBuilder.put(DataType.SHORT, DataOrder.LITTLE, flags);
			} else {
				blockBuilder.put(DataType.BYTE, flags);
			}
			// TODO:

			// Block order:
			// 0x1000??
			encodeBlock(message, blockBuilder, TransformationNpcBlock.class);// 0x10
			encodeBlock(message, blockBuilder, SecondaryDamageNpcBlock.class);// 2
			// 0x400 face coordinates
			// 0x100 idk, force movement?
			// 0x800 spotanim true
			encodeBlock(message, blockBuilder, AnimationNpcBlock.class);// 4
			// 0x2000 idk again
			encodeBlock(message, blockBuilder, SpotAnimationNpcBlock.class);// 1false
			// 0x200
//			encodeBlock(message, blockBuilder, OrientateToMobNpcBlock.class);// 8 face
			encodeBlock(message, blockBuilder, NpcShoutBlock.class);// 0x80
			encodeBlock(message, blockBuilder, PrimaryDamageNpcBlock.class);// 0x20
		}
	}

	private void encodeBlock(NpcUpdateMessage message, PacketBuilder builder, Class<? extends NpcBlock> type) {
		NpcBlock block = blocks.get(type);
		if (block != null)
			block.encode(message, builder);
	}

	public abstract void encodeDescriptor(NpcUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder);

}
