package net.scapeemulator.game.update.player;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.MobList;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.area.StaticAreaMessage;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.update.Descriptor;
import net.scapeemulator.game.update.MobUpdater;
import net.scapeemulator.game.update.player.descr.RemovePlayerDescriptor;
import net.scapeemulator.game.update.player.descr.external.AddPlayerDescriptor;
import net.scapeemulator.game.update.player.descr.external.ExternalDescriptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlayerUpdater extends MobUpdater<Player> {

	public PlayerUpdater(MobList<Player> mobs) {
		super(mobs);
	}

	@Override
	protected void preprocess(Player player) {
		// if (player.getWalkingQueue().isMinimapFlagReset())
		// player.nonsend(new ResetMinimapFlagMessage());
		if (isRegionChangeRequired(player)) {
			Position position = player.getPosition();
			player.setLastKnownRegion(position);
			player.send(new StaticAreaMessage(position, player.getFov(), true));
		}
		player.getStatus().refresh();
	}

	@Override
	public void process(Player player) {
		Position lastKnownRegion = player.getLastKnownRegion();
		Position position = player.getPosition();
		List<Descriptor<PlayerUpdateMessage>> descriptors = new ArrayList<>();
		Pair localPair = localUpdate(player);
		Pair externalPair = externalUpdate(player);
		descriptors.addAll(localPair.descriptors);
		descriptors.addAll(externalPair.descriptors);
		player.getLocalPlayers().removeAll(localPair.victims);
		player.getLocalPlayers().addAll(externalPair.victims);
		player.send(new PlayerUpdateMessage(lastKnownRegion, position, descriptors));
	}

	@Override
	protected void postProcess(Player player) {
		Collections.sort(player.getLocalPlayers());
		int[] tickets = player.getUpdateTickets();
		for (int i = 0; i < tickets.length; i++) {
			tickets[i] >>= 1;
		}
	}

	private Pair localUpdate(Player player) {
		List<Descriptor<PlayerUpdateMessage>> outer = newCycle(), inner = newCycle();
		List<Player> localPlayers = player.getLocalPlayers();
		List<Player> removables = new ArrayList<>();
		int[] tickets = player.getAppearanceTickets();
		int innerSkip = -1, outerSkip = -1;
		for (Player local : localPlayers) {
			boolean innerCycle = true;
			if ((player.getUpdateTickets()[local.getId()] & 1) == 0) {
				innerCycle = false;
			}
			Descriptor<PlayerUpdateMessage> descriptor = PlayerDescriptor.create(local, tickets, player);
			if (descriptor == null) {
				if (innerCycle) {
					innerSkip++;
				} else {
					outerSkip++;
				}
				player.getUpdateTickets()[local.getId()] |= 2;
				continue;
			} else {
				List<Descriptor<PlayerUpdateMessage>> descr = innerCycle ? inner : outer;
				if (innerCycle && innerSkip != -1) {
					descr.add(new SkipDescriptor(innerSkip));
					innerSkip = -1;
				} else if (!innerCycle && outerSkip != -1) {
					descr.add(new SkipDescriptor(outerSkip));
					outerSkip = -1;
				}
				descr.add(descriptor);
				if (descriptor instanceof RemovePlayerDescriptor)
					removables.add(local);
			}
		}
		if (innerSkip != -1) {
			inner.add(new SkipDescriptor(innerSkip));
		}
		if (outerSkip != -1) {
			outer.add(new SkipDescriptor(outerSkip));
		}
		finishCycles(outer, inner);
		return new Pair(outer, removables);
	}

	private Pair externalUpdate(Player local) {
		List<Descriptor<PlayerUpdateMessage>> outer = newCycle(), inner = newCycle();
		List<Player> localPlayers = local.getLocalPlayers();
		List<Player> newLocals = new ArrayList<>();
		int[] tickets = local.getAppearanceTickets();
		int innerSkip = -1, outerSkip = -1;
		for (int id = 1; id < 2048; id++) {
			boolean innerCycle = true;
			Player player = getMobs().get(id);
			if (localPlayers.contains(player)) {
				continue;
			}
			if ((local.getUpdateTickets()[id] & 1) == 0) {
				innerCycle = false;
			}
			Descriptor<PlayerUpdateMessage> descriptor = ExternalDescriptor.create(player);

			boolean newPlayer = player != null && player.isListed()
					&& local.getPosition().isWithinDistance(player.getPosition());
			// && player.getInstance() == local.getInstance()
			if (newPlayer && local.getPosition().getHeight() != player.getPosition().getHeight())
				newPlayer = false;
			if (descriptor == null && !newPlayer) {
				if (innerCycle) {
					innerSkip++;
				} else {
					outerSkip++;
				}
				local.getUpdateTickets()[id] |= 2;
				continue;
			} else {
				System.out.println("EXTERNAL: " + local.getUsername());
				List<Descriptor<PlayerUpdateMessage>> descr = innerCycle ? inner : outer;
				if (innerCycle && innerSkip != -1) {
					descr.add(new SkipDescriptor(innerSkip));
					innerSkip = -1;
				} else if (!innerCycle && outerSkip != -1) {
					descr.add(new SkipDescriptor(outerSkip));
					outerSkip = -1;
				}
				if (newPlayer) {
					descriptor = new AddPlayerDescriptor(player, tickets, (ExternalDescriptor) descriptor);
					local.getUpdateTickets()[id] |= 2;
					newLocals.add(player);
				}
				descr.add(descriptor);
			}
		}
		if (innerSkip != -1) {
			inner.add(new SkipDescriptor(innerSkip));
		}
		if (outerSkip != -1) {
			outer.add(new SkipDescriptor(outerSkip));
		}
		finishCycles(inner, outer);
		return new Pair(inner, newLocals);
	}

	private List<Descriptor<PlayerUpdateMessage>> newCycle() {
		List<Descriptor<PlayerUpdateMessage>> descr = new ArrayList<>();
		descr.add(new CycleStartDescriptor());
		return descr;
	}

	@SafeVarargs
	private static final void finishCycles(List<Descriptor<PlayerUpdateMessage>>... lists) {
		List<Descriptor<PlayerUpdateMessage>> previous = null;
		for (List<Descriptor<PlayerUpdateMessage>> list : lists) {
			list.add(new CycleEndDescriptor());
			if (previous != null)
				previous.addAll(list);
			previous = list;
		}
	}

	private boolean isRegionChangeRequired(Player player) {
		int deltaX = player.getLocalX();
		int deltaY = player.getLocalY();
		int fov = player.getFov().getTiles() - 16;
		return deltaX < 16 || deltaX >= fov || deltaY < 16 || deltaY >= fov;
	}

	public static int translate(int previous, int next, int top) {
		if (previous < next) {
			return next - previous;
		} else if (previous > next) {
			return top - previous + next;
		}
		return 0;
	}

	private static final class Pair {
		private List<Descriptor<PlayerUpdateMessage>> descriptors;
		private List<Player> victims;

		Pair(List<Descriptor<PlayerUpdateMessage>> descriptors, List<Player> victims) {
			this.descriptors = descriptors;
			this.victims = victims;
		}
	}
}
