package net.scapeemulator.game.update.npc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.MobList;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.NpcUpdateMessage;
import net.scapeemulator.game.update.MobUpdater;
import net.scapeemulator.game.update.npc.descr.AddNpcDescriptor;
import net.scapeemulator.game.update.npc.descr.RemoveNpcDescriptor;

public class NpcUpdater extends MobUpdater<Npc> {

	public NpcUpdater(MobList<Npc> mobs) {
		super(mobs);
	}

	@Override
	public void process(Player player) {
		Position lastKnownRegion = player.getLastKnownRegion();
		Position position = player.getPosition();

		List<NpcDescriptor> descriptors = new ArrayList<>();
		List<Npc> localNpcs = player.getLocalNpcs();
		int localNpcCount = localNpcs.size();

		for (Iterator<Npc> it = localNpcs.iterator(); it.hasNext();) {
			Npc n = it.next();
			if (!n.isListed() || n.isTeleporting() || !position.isWithinDistance(n.getPosition())) {
				it.remove();
				descriptors.add(new RemoveNpcDescriptor(n));
			} else {
				descriptors.add(NpcDescriptor.create(n));
			}
		}

		for (Npc n : getMobs()) {
			if (localNpcs.size() >= 255)
				break;

			if (position.isWithinDistance(n.getPosition()) && !localNpcs.contains(n)) {
				localNpcs.add(n);
				descriptors.add(new AddNpcDescriptor(n));
			}
		}

		player.send(new NpcUpdateMessage(lastKnownRegion, position, localNpcCount, descriptors));
	}

	@Override
	protected void postProcess(Npc mob) {
		mob.reset();
	}

	@Override
	protected void preprocess(Npc mob) {

	}
}
