package net.scapeemulator.game.update.player;

import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.Descriptor;
import net.scapeemulator.game.update.player.block.AnimationPlayerBlock;
import net.scapeemulator.game.update.player.block.AppearancePlayerBlock;
import net.scapeemulator.game.update.player.block.ChatPlayerBlock;
import net.scapeemulator.game.update.player.block.OrientateToCoordinatesPlayerBlock;
import net.scapeemulator.game.update.player.block.OrientateToMobPlayerBlock;
import net.scapeemulator.game.update.player.block.PlayerShoutBlock;
import net.scapeemulator.game.update.player.block.PrimaryDamagePlayerBlock;
import net.scapeemulator.game.update.player.block.SecondaryDamagePlayerBlock;
import net.scapeemulator.game.update.player.block.SpotAnimationPlayerBlock;
import net.scapeemulator.game.update.player.block.TemporaryMovementTypeBlock;
import net.scapeemulator.game.update.player.descr.IdlePlayerDescriptor;
import net.scapeemulator.game.update.player.descr.RemovePlayerDescriptor;
import net.scapeemulator.game.update.player.descr.RunPlayerDescriptor;
import net.scapeemulator.game.update.player.descr.TeleportPlayerDescriptor;
import net.scapeemulator.game.update.player.descr.WalkPlayerDescriptor;
import net.scapeemulator.game.update.player.descr.external.ExternalDescriptor;

import java.util.HashMap;
import java.util.Map;

public abstract class PlayerDescriptor extends Descriptor<PlayerUpdateMessage> {

	public static Descriptor<PlayerUpdateMessage> create(Player player, int[] tickets, Player local) {
		if (!player.isListed() || !player.getPosition().isWithinDistance(local.getPosition())) {
			// || player.getInstance() != local.getInstance()
			ExternalDescriptor descriptor = ExternalDescriptor.create(player);
			return new RemovePlayerDescriptor(descriptor);
		}
		// TODO: Movement types
		Position walkStep = player.getWalkStep();
		PlayerDescriptor descriptor;
		if (player.isTeleporting()) {
			descriptor = new TeleportPlayerDescriptor(player, tickets);
			descriptor.addBlock(new TemporaryMovementTypeBlock(player, 127));
			return descriptor;
		} else {
			if (walkStep == null) {
				descriptor = new IdlePlayerDescriptor(player, tickets);
				return descriptor.isBlockUpdatedRequired() ? descriptor : null;
			} else {
				int dir = RunPlayerDescriptor.convert(player);
				if (dir != -1) {
					descriptor = new RunPlayerDescriptor(player, tickets, dir);
					descriptor.addBlock(new TemporaryMovementTypeBlock(player, 2));
					return descriptor;
				} else {
					descriptor = new WalkPlayerDescriptor(player, tickets);
					descriptor.addBlock(new TemporaryMovementTypeBlock(player, 1));
					return descriptor;
				}
			}
		}
	}

	private final Map<Class<? extends PlayerBlock>, PlayerBlock> blocks = new HashMap<>();

	public PlayerDescriptor(Player player, int[] tickets) {
		if (player.isListed()) {
			/*
			 * This active check is required for the RemovePlayerDescriptor. The
			 * player id would be -1 in this case, which causes the following
			 * code to crash. Skipping this code doesn't matter as no update
			 * blocks can be sent when removing a player.
			 */
			int id = player.getId() - 1;
			int ticket = player.getAppearanceTicket();
			if (tickets[id] != ticket) {
				tickets[id] = ticket;
				addBlock(new AppearancePlayerBlock(player));
			}
		}
		if (player.isChatUpdated())
			addBlock(new ChatPlayerBlock(player));

		if (player.isAnimationUpdated())
			addBlock(new AnimationPlayerBlock(player));

		if (player.isSpotAnimationUpdated())
			addBlock(new SpotAnimationPlayerBlock(player));

		if (player.getShout() != null)
			addBlock(new PlayerShoutBlock(player));

		if (player.getOrientationPosition() != null)
			addBlock(new OrientateToCoordinatesPlayerBlock(player));

		if (player.isTargetUpdated())
			addBlock(new OrientateToMobPlayerBlock(player));

		if (player.getPrimaryHit() != null)
			addBlock(new PrimaryDamagePlayerBlock(player));

		if (player.getSecondaryHit() != null)
			addBlock(new SecondaryDamagePlayerBlock(player));
	}

	private void addBlock(PlayerBlock block) {
		blocks.put(block.getClass(), block);
	}

	public boolean isBlockUpdatedRequired() {
		return !blocks.isEmpty();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder, PacketBuilder blockBuilder) {
		encodeDescriptor(message, builder, blockBuilder);

		if (isBlockUpdatedRequired()) {
			int flags = 0;
			for (PlayerBlock block : blocks.values())
				flags |= block.getFlag();

			if (flags > 0xFF) {
				flags |= 4;
				if (flags > 0xFFFF) {
					flags |= 0x2000;
					blockBuilder.put(DataType.TRI_BYTE, DataOrder.LITTLE, flags);
				} else {
					blockBuilder.put(DataType.SHORT, DataOrder.LITTLE, flags);
				}
			} else {
				blockBuilder.put(DataType.BYTE, flags);
			}

			encodeBlock(message, blockBuilder, OrientateToCoordinatesPlayerBlock.class);// 0x40
			encodeBlock(message, blockBuilder, PlayerShoutBlock.class);// 0x400
			// 0x8000 forced movement?
			// 0x800 ???
			// 0x10000 AnimGroup
			// 0x40000 spotAnimTrue

			// encodeBlock(message, blockBuilder,
			// PrimaryMovementTypeBlock.class);//0x1

			encodeBlock(message, blockBuilder, OrientateToMobPlayerBlock.class);// 2
			encodeBlock(message, blockBuilder, ChatPlayerBlock.class);// 0x80
			encodeBlock(message, blockBuilder, SpotAnimationPlayerBlock.class);// 0x1000false
			encodeBlock(message, blockBuilder, AnimationPlayerBlock.class);// 8
			// 0x20000 ???
			encodeBlock(message, blockBuilder, TemporaryMovementTypeBlock.class);
			encodeBlock(message, blockBuilder, PrimaryDamagePlayerBlock.class);// 0x10
			// 0x4000 boolean??
			encodeBlock(message, blockBuilder, AppearancePlayerBlock.class);// 0x20
			encodeBlock(message, blockBuilder, SecondaryDamagePlayerBlock.class);// 0x100
		}
	}

	private void encodeBlock(PlayerUpdateMessage message, PacketBuilder builder, Class<? extends PlayerBlock> type) {
		PlayerBlock block = blocks.get(type);
		if (block != null)
			block.encode(message, builder);
	}

	public abstract void encodeDescriptor(PlayerUpdateMessage message, PacketBuilder builder,
			PacketBuilder blockBuilder);
}
