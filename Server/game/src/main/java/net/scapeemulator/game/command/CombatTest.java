package net.scapeemulator.game.command;

import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.Combat;
import net.scapeemulator.game.model.mob.SpotAnimation;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.Player;

public final class CombatTest extends CommandHandler {

	public CombatTest() {
		super("combat");
	}

	@Override
	public void handle(Player player, String[] arguments) {
		if (player.getRights() < 2)
			return;
		try {
			switch (arguments[0]) {
			case "att":
				player.playAnimation(player.getCombat().getAttackAnimations()[player.getCombat().getCurrentStyle()]);
				break;
			case "defend":
				player.playAnimation(player.getCombat().getDefAnim());
				break;
			case "anim":
				player.playAnimation(new Animation(Integer.parseInt(arguments[1])));
				break;
			case "gfx":
				player.playSpotAnimation(new SpotAnimation(Integer.parseInt(arguments[1]), 0, 100));
				break;
			case "shout":
				player.shout(arguments[1]);
				break;
			case "nshout":
				Npc n = World.getWorld().getNpcs().get(Integer.parseInt(arguments[1]));
				n.shout(arguments[2]);
				break;
			case "dmg":
				player.addHit(
						new Hit(Hit.Type.values()[Integer.parseInt(arguments[1])], Integer.parseInt(arguments[2])));
				break;
			case "ndmg":
				n = World.getWorld().getNpcs().get(Integer.parseInt(arguments[1]));
				n.addHit(new Hit(Hit.Type.values()[Integer.parseInt(arguments[2])], Integer.parseInt(arguments[3])));
				break;
			case "max":
			case "max_raw":
				player.sendMessage("Maximum hit on affecting weapon: " + Combat.getMaxHit(player, 1.0));
				break;
			case "p":
				Player other = World.getWorld().getPlayers().get(Integer.parseInt(arguments[1]));
				player.getCombat().setTarget(other);
				break;
			case "n":
				n = World.getWorld().getNpcs().get(Integer.parseInt(arguments[1]));
				player.getCombat().setTarget(n);
				break;
			case "nvp":
				n = World.getWorld().getNpcs().get(Integer.parseInt(arguments[1]));
				other = World.getWorld().getPlayers().get(Integer.parseInt(arguments[2]));
				n.getCombat().setTarget(other);
				break;
			case "nvn":
				n = World.getWorld().getNpcs().get(Integer.parseInt(arguments[1]));
				Npc n2 = n = World.getWorld().getNpcs().get(Integer.parseInt(arguments[2]));
				n.getCombat().setTarget(n2);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			player.sendMessage("Oops! Something went wrong.", 99);
		}
	}
}
