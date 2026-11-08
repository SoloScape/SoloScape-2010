package net.scapeemulator.game.model.mob;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.orb.RunOrb;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.ChildOptionClickMessageDecoder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RestingTest {
	private Player player;

	@Before
	public void setUp() {
		player = new Player();
		player.setPosition(new Position(3200, 3200));
		player.setEnergy(0);
	}

	private void clickRest() throws Exception {
		ByteBuf payload = Unpooled.buffer(8).writeInt((750 << 16) | 1).writeInt(-1);
		try {
			ChildInterfaceClickAction click = new ChildOptionClickMessageDecoder(2).decode(
					new Packet(38, Packet.Type.FIXED, payload));
			RunOrb.INSTANCE.dispatch(player, click);
		} finally {
			payload.release();
		}
	}

	@Test
	public void restMenuSitsDownAndClearsExistingRoute() throws Exception {
		player.getMovement().addFirstStep(new Position(3210, 3200));
		clickRest();
		assertTrue(player.isResting());
		assertEquals(5713, player.getAnimation().getId());
		player.getStatus().refresh();
		assertEquals(3, player.getStatus().getCurrentVar(173));
		player.getMovement().tick();
		assertEquals(new Position(3200, 3200), player.getPosition());
		assertNull(player.getWalkStep());
		assertEquals(1, player.getEnergy());
	}

	@Test
	public void restingReconstructsReportedRecoveryTimes() throws Exception {
		assertEquals(59, recoverWhileResting(1));
		assertEquals(42, recoverWhileResting(50));
		assertEquals(25, recoverWhileResting(99));
	}

	@Test
	public void movingCancelsRestBeforeAnyRecoveryBonus() throws Exception {
		player.setEnergy(50);
		clickRest();
		player.getMovement().addFirstStep(new Position(3210, 3200));
		player.getMovement().tick();
		assertFalse(player.isResting());
		assertEquals(50, player.getEnergy());
		assertEquals(new Position(3201, 3200), player.getPosition());
	}

	@Test
	public void walkingActionCancellationStandsUpAndRestoresRunPreference() throws Exception {
		player.setEnergy(50);
		player.setRunning(true);
		clickRest();
		assertFalse(player.isRunning());
		player.stopAction();
		assertFalse(player.isResting());
		assertEquals(5748, player.getAnimation().getId());
		assertTrue(player.isRunning());
	}

	@Test
	public void runButtonCancelsRest() throws Exception {
		player.setEnergy(50);
		clickRest();
		RunOrb.INSTANCE.dispatch(player, new ChildInterfaceClickAction(1, 750, 1, -1, -1));
		assertFalse(player.isResting());
		assertTrue(player.isRunning());
	}

	@Test
	public void hitsAndNewAnimationsCancelRest() throws Exception {
		clickRest();
		player.addHit(new Hit(Hit.Type.miss, 0));
		assertFalse(player.isResting());
		clickRest();
		player.playAnimation(new Animation(863));
		assertFalse(player.isResting());
		assertEquals(863, player.getAnimation().getId());
	}

	@Test
	public void cannotStartRestWhileAttacking() throws Exception {
		player.getCombat().setTarget(new Player());
		clickRest();
		assertFalse(player.isResting());
		assertNull(player.getAnimation());
	}

	@Test
	public void acquiringCombatTargetRemovesRecoveryBonus() throws Exception {
		clickRest();
		player.getCombat().setTarget(new Player());
		player.getMovement().tick();
		assertFalse(player.isResting());
		assertEquals(0, player.getEnergy());
	}

	@Test
	public void teleportCancelsRest() throws Exception {
		clickRest();
		player.teleport(new Position(3210, 3210));
		assertFalse(player.isResting());
		assertEquals(0, player.getStatus().getWaitingVar(173));
	}

	@Test
	public void repeatedRestDoesNotLoseOriginalRunPreference() throws Exception {
		player.setEnergy(50);
		player.setRunning(true);
		clickRest();
		clickRest();
		player.stopResting();
		assertTrue(player.isRunning());
	}

	@Test
	public void fullEnergyCanStaySeatedAndDoesNotOverflow() throws Exception {
		player.setEnergy(100);
		clickRest();
		player.reset();
		for (int i = 0; i < 80; i++)
			player.getMovement().tick();
		assertTrue(player.isResting());
		assertEquals(100, player.getEnergy());
		assertEquals(5713, player.getAnimation().getId());
	}

	private int recoverWhileResting(int agility) throws Exception {
		setUp();
		player.getSkillSet().restore(Skill.AGILITY, agility - 1, true);
		clickRest();
		int ticks = 0;
		while (player.getEnergy() < 100 && ticks < 100) {
			player.getMovement().tick();
			ticks++;
		}
		assertEquals(100, player.getEnergy());
		return ticks;
	}
}
