package net.scapeemulator.game.model.mob;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;
import net.scapeemulator.game.model.hud.orb.RunOrb;
import net.scapeemulator.game.model.hud.settings.SettingsTab;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.interf.ChildClickDecoder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class RunMovementTest {
	private Player player;

	@Before
	public void setUp() {
		player = new Player();
		player.setPosition(new Position(3200, 3200));
		player.getMovement().addFirstStep(new Position(3210, 3200));
	}

	private void clickRunOrb() {
		RunOrb.INSTANCE.dispatch(player, new ChildInterfaceClickAction(1, 750, 1, -1, -1));
	}

	private void clickSettingsRun() {
		ByteBuf payload = Unpooled.buffer(6).writeInt((261 << 16) | 3).writeShort(-1);
		try {
			ChildInterfaceClickAction action = new ChildClickDecoder().decode(
					new Packet(18, Packet.Type.FIXED, payload));
			SettingsTab.INSTANCE.dispatch(player, action);
		} finally {
			payload.release();
		}
	}

	@Test
	public void settingsRunClickEnablesActualRunning() {
		clickSettingsRun();
		player.getStatus().refresh();
		player.getMovement().tick();
		assertTrue(player.isRunning());
		assertEquals(1, player.getStatus().getCurrentVar(173));
		assertEquals(new Position(3202, 3200), player.getPosition());
	}

	@Test
	public void settingsAndOrbShareToggleStateBeforeStatusRefresh() {
		clickRunOrb();
		clickSettingsRun();
		assertFalse(player.isRunning());
		player.getMovement().tick();
		assertEquals(new Position(3201, 3200), player.getPosition());
		clickSettingsRun();
		assertTrue(player.isRunning());
		clickRunOrb();
		assertFalse(player.isRunning());
		player.getStatus().refresh();
		assertEquals(0, player.getStatus().getCurrentVar(173));
	}

	@Test
	public void settingsCannotEnableRunWithoutEnergy() {
		player.setEnergy(0);
		clickSettingsRun();
		player.getStatus().refresh();
		assertFalse(player.isRunning());
		assertEquals(0, player.getStatus().getCurrentVar(173));
	}

	@Test
	public void walksByDefault() {
		player.getMovement().tick();
		assertEquals(new Position(3201, 3200), player.getPosition());
		assertNull(player.getRunStep());
	}

	@Test
	public void orbEnablesRunningAndSynchronizesClientState() {
		clickRunOrb();
		player.getStatus().refresh();
		assertTrue(player.isRunning());
		assertEquals(1, player.getStatus().getCurrentVar(173));
		player.getMovement().tick();
		assertEquals(new Position(3202, 3200), player.getPosition());
		assertEquals(new Position(3201, 3200), player.getWalkStep());
		assertEquals(new Position(3202, 3200), player.getRunStep());
	}

	@Test
	public void orbCanChangeSpeedDuringAnExistingRoute() {
		player.getMovement().tick();
		clickRunOrb();
		player.getMovement().tick();
		assertEquals(new Position(3203, 3200), player.getPosition());
		clickRunOrb();
		player.getStatus().refresh();
		player.getMovement().tick();
		assertEquals(new Position(3204, 3200), player.getPosition());
		assertNull(player.getRunStep());
		assertEquals(0, player.getStatus().getCurrentVar(173));
	}

	@Test
	public void repeatedClicksBeforeRefreshDoNotLoseToggle() {
		clickRunOrb();
		clickRunOrb();
		assertFalse(player.isRunning());
		player.getStatus().refresh();
		assertEquals(0, player.getStatus().getCurrentVar(173));
	}

	@Test
	public void runningStopsAtLastTileWithoutOvershooting() {
		player.getMovement().addFirstStep(new Position(3201, 3200));
		clickRunOrb();
		player.getMovement().tick();
		assertEquals(new Position(3201, 3200), player.getPosition());
		assertNull(player.getRunStep());
		player.getMovement().tick();
		assertEquals(new Position(3201, 3200), player.getPosition());
		assertNull(player.getWalkStep());
	}

	@Test
	public void forcedRunRouteStillWorksWithOrbOff() {
		player.getMovement().setRunningQueue(true);
		player.getMovement().tick();
		assertEquals(new Position(3202, 3200), player.getPosition());
		assertFalse(player.isRunning());
	}

	@Test
	public void runningDrainsEnergyAndRetainsFractions() {
		clickRunOrb();
		player.getMovement().tick();
		assertEquals(99, player.getEnergy());
		player.getMovement().tick();
		assertEquals(98, player.getEnergy());
		player.getMovement().tick();
		assertEquals(97, player.getEnergy());
		player.getMovement().tick();
		assertEquals(96, player.getEnergy());
		// Four ticks cost about 3.87%, rather than rounding every tick to 1%.
		player.setRunning(false);
		player.getMovement().tick();
		assertEquals(96, player.getEnergy());
	}

	@Test
	public void idleAndWalkingRecoverAtTheSameRateWithRunToggleOn() {
		player.setEnergy(50);
		player.getMovement().reset();
		clickRunOrb();
		for (int i = 0; i < 4; i++)
			player.getMovement().tick();
		assertEquals(51, player.getEnergy());
		player.setRunning(false);
		player.getMovement().addFirstStep(new Position(3210, 3200));
		for (int i = 0; i < 4; i++)
			player.getMovement().tick();
		assertEquals(52, player.getEnergy());
	}

	@Test
	public void singleTileDestinationDoesNotDrainEnergy() {
		player.setEnergy(50);
		player.getMovement().addFirstStep(new Position(3201, 3200));
		clickRunOrb();
		player.getMovement().tick();
		assertEquals(50, player.getEnergy());
		assertNull(player.getRunStep());
	}

	@Test
	public void exhaustionTurnsOffOrbAndForcedRunningWithoutLosingRoute() {
		player.setEnergy(1);
		clickRunOrb();
		player.getMovement().setRunningQueue(true);
		player.getMovement().tick();
		player.getMovement().tick();
		assertEquals(0, player.getEnergy());
		assertFalse(player.hasRunEnergy());
		assertFalse(player.isRunning());
		assertFalse(player.getMovement().isRunningQueue());
		player.getStatus().refresh();
		assertEquals(0, player.getStatus().getCurrentVar(173));
		player.getMovement().tick();
		assertEquals(new Position(3205, 3200), player.getPosition());
		assertNull(player.getRunStep());
	}

	@Test
	public void zeroEnergyCannotBeBypassedByOrbOrForcedRun() {
		player.setEnergy(0);
		clickRunOrb();
		player.getMovement().setRunningQueue(true);
		player.getMovement().tick();
		assertEquals(new Position(3201, 3200), player.getPosition());
		assertNull(player.getRunStep());
		assertFalse(player.isRunning());
	}

	@Test
	public void recoveryDoesNotAutomaticallyReenableRun() {
		player.setEnergy(0);
		player.getMovement().reset();
		for (int i = 0; i < 4; i++)
			player.getMovement().tick();
		assertEquals(1, player.getEnergy());
		assertFalse(player.isRunning());
		clickRunOrb();
		assertTrue(player.isRunning());
	}

	@Test
	public void reconstructedRunDurationsScaleWithCurrentAgility() {
		assertEquals(104, runUntilExhausted(1));
		assertEquals(131, runUntilExhausted(50));
		assertEquals(159, runUntilExhausted(99));
	}

	@Test
	public void reconstructedRecoveryDurationsScaleWithCurrentAgility() {
		assertEquals(375, recoverFully(1));
		assertEquals(294, recoverFully(50));
		assertEquals(212, recoverFully(99));
	}

	@Test
	public void externalEnergyChangesClampAndDisableRunning() {
		player.setEnergy(500);
		assertEquals(100, player.getEnergy());
		clickRunOrb();
		player.setEnergy(-50);
		assertEquals(0, player.getEnergy());
		assertFalse(player.isRunning());
		player.setEnergy(100);
		assertFalse(player.isRunning());
	}

	@Test
	public void passiveRecoveryCapsAtFullEnergy() {
		player.setEnergy(99);
		player.getMovement().reset();
		for (int i = 0; i < 100; i++)
			player.getMovement().tick();
		assertEquals(100, player.getEnergy());
	}

	@Test
	public void drainedAndOverboostedAgilityUseSupportedBounds() {
		assertEquals(runUntilExhausted(1), runUntilExhausted(0));
		assertEquals(runUntilExhausted(99), runUntilExhausted(120));
	}

	private int runUntilExhausted(int agility) {
		setUp();
		player.getSkillSet().restore(Skill.AGILITY, agility - 1, true);
		player.getMovement().addFirstStep(new Position(4200, 3200));
		clickRunOrb();
		int ticks = 0;
		while (player.hasRunEnergy() && ticks < 1000) {
			player.getMovement().tick();
			ticks++;
		}
		assertFalse(player.hasRunEnergy());
		return ticks;
	}

	private int recoverFully(int agility) {
		setUp();
		player.getSkillSet().restore(Skill.AGILITY, agility - 1, true);
		player.getMovement().reset();
		player.setEnergy(0);
		int ticks = 0;
		while (player.getEnergy() < 100 && ticks < 1000) {
			player.getMovement().tick();
			ticks++;
		}
		return ticks;
	}
}
