package net.scapeemulator.game.model.tele;

import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.player.Player;

public class ModernTeleport extends Teleport {
	private int tick;

	public ModernTeleport(Player player, Position position) {
		super(player, position, 1);
	}

	@Override
	public void execute() {
		switch (tick) {
		case 0:
			mob.playAnimation(new Animation(8939));
			mob.playSpotAnimation(new SpotAnimation(1576));
			break;
		case 3:
			mob.teleport(target);
			mob.playAnimation(new Animation(8941));
			mob.playSpotAnimation(new SpotAnimation(1577));
			break;
		case 5:
			stop();
			break;
		}
		tick++;
	}
}
