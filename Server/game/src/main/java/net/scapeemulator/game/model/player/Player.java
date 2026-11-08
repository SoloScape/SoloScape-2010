package net.scapeemulator.game.model.player;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.game.model.FieldOfView;
import net.scapeemulator.game.model.World;
import net.scapeemulator.game.model.constants.Interfaces;
import net.scapeemulator.game.model.constants.Skill;
import net.scapeemulator.game.model.entity.Position;
import net.scapeemulator.game.model.hud.HeadsUpDisplay;
import net.scapeemulator.game.model.hud.interf.Tab;
import net.scapeemulator.game.model.hud.combat.PrayerTab;
import net.scapeemulator.game.model.mob.Mob;
import net.scapeemulator.game.model.mob.Animation;
import net.scapeemulator.game.model.mob.combat.Hit;
import net.scapeemulator.game.model.npc.Npc;
import net.scapeemulator.game.model.player.var.Status;
import net.scapeemulator.game.msg.CachedMessage;
import net.scapeemulator.game.msg.gameplay.Blackout;
import net.scapeemulator.game.msg.gameplay.ChatMessage;
import net.scapeemulator.game.msg.gameplay.EnergyMessage;
import net.scapeemulator.game.msg.interf.util.GlobalIntVarMessage;
import net.scapeemulator.game.msg.util.LogoutMessage;
import net.scapeemulator.game.msg.util.ServerMessage;
import net.scapeemulator.game.net.game.GameSession;
import net.scapeemulator.game.task.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Player extends Mob {
	private static final int RUN_MODE_VAR = 173;

	// TODO: Temporary attributes.
	private static int appearanceTicketCounter = 0;

	private static int nextAppearanceTicket() {
		if (++appearanceTicketCounter == 0)
			appearanceTicketCounter = 1;

		return appearanceTicketCounter;
	}

	private String username;
	private String password;
	private int rights = 0;

	private GameSession session;
	private int databaseId;

	private final HeadsUpDisplay display = new HeadsUpDisplay(this);
	private final InventoryContainer inventories = new InventoryContainer(this);
	private final Status status = new Status(this);

	private FieldOfView fov = FieldOfView.REGULAR;
	private Position lastKnownRegion;
	private boolean regionChanging;
	private final List<Player> localPlayers = new ArrayList<>();
	private final List<Npc> localNpcs = new ArrayList<>();

	private Appearance appearance = Appearance.DEFAULT_APPEARANCE;
	private boolean characterCreationRequired;
	private int characterCreationCategory;
	private boolean controlsTutorialPending;
	private int controlsTutorialOpeningDelay;
	private long controlsTutorialShownAt;
	// Only first-time tutorial characters have gated tabs. Old profiles are unrestricted.
	private boolean tutorialTabsRestricted;
	private int tutorialTabMask;
	private boolean cameraPositionKnown;
	private int lastCameraYaw, lastCameraPitch;
	private int[] appearanceTickets = new int[World.MAX_PLAYERS];
	private int[] tickets = new int[World.MAX_PLAYERS];
	private int appearanceTicket = nextAppearanceTicket();
	private ChatMessage chatMessage;

	private Blackout blackout = Blackout.NONE;
	private int sequenceNumber = 0;
	private double energy = 100;
	private boolean resting, runBeforeRest;
	private int restAnimationTicks;

	// Could the sequence number be a 'ping' to declare that a message was
	// reached to the client? If it was, it could be easily used to send the
	// data not reached the client, to the client.
	private final Map<Integer, CachedMessage> cached = new HashMap<>();
	private int latestSequencenumber;
	private int stance = 1426;

	private int specialRestore = 30;

	/*
	 * TODO: Skull
	 */

	public Player() {
		init();
	}

	@Override
	public void restore() {
		super.restore();
		status.setStatus("special_attack_power", 1000);
		status.setStatus("prayer", 0);
		
		// Curse reset
		// Poison reset
		// Bonus reset
	}

	@Override
	public void processEffects() {
		/*
		 * if inter_open return;
		 */
		super.processEffects();
		if ((status.getCurrentVar(PrayerTab.PRAYER) & (1 << PrayerTab.RAPID_RESTORE)) != 0) {
			statRestoreCounter--;
		}
		if ((status.getCurrentVar(PrayerTab.PRAYER) & (1 << PrayerTab.RAPID_HEAL)) != 0) {
			healthRestoreCounter--;
		}
		if (specialRestore <= 0) {
			specialRestore = 30;
			status.increment("special_attack_power", 100);
		}
		specialRestore--;
		/**
		 * TODO: Prayer drain
		 */
	}

	private void init() {
		skillSet.addListener(new SkillMessageListener(this));
		skillSet.addListener(new SkillAppearanceListener(this));
	}

	public ChannelFuture send(Message message) {
		if (message instanceof CachedMessage) {
			sequenceNumber = 1 + sequenceNumber & 0xffff;
			CachedMessage seqMessage = ((CachedMessage) message);
			seqMessage.setSequenceNumber(sequenceNumber);
			synchronized (cached) {
				cached.put(sequenceNumber, seqMessage);
			}
		}
		if (session != null) {
			return session.send(message);
		} else
			return null;
	}

	public void logout() {
		// TODO: Logout
		ChannelFuture future = send(new LogoutMessage());
		if (future != null)
			future.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					future.channel().close();
				}
			});

	}

	@Override
	public String toString() {
		return "Player [username=" + username + ", rights=" + rights + ", position=" + position + "]";
	}

	public void packetsReached(int lastReceivedSequenceNumber) {
		synchronized (cached) {
			if (lastReceivedSequenceNumber < latestSequencenumber) {
				for (int i = latestSequencenumber; i < 0xffff; i++) {
					cached.remove(i);
					latestSequencenumber = 0;
				}
			}
			for (int i = latestSequencenumber; i < lastReceivedSequenceNumber; i++) {
				cached.remove(i);
			}
			latestSequencenumber = lastReceivedSequenceNumber;
		}
	}

	@Override
	public void reset() {
		super.reset();
		regionChanging = false;
		chatMessage = null;
		orientationPosition = null;

		// TODO should this be done a different way?
		interactingTarget = null;
	}

	@Override
	public boolean isRunning() {
		return hasRunEnergy() && status.getWaitingVar(RUN_MODE_VAR) == 1;
	}

	public void setRunning(boolean running) {
		stopResting();
		status.setVar(RUN_MODE_VAR, running && getEnergy() > 0 ? 1 : 0);
	}

	public boolean isResting() {
		return resting;
	}

	public void startResting() {
		if (resting)
			return;
		if (dead() || blockingAction() || combat.getTarget() != null || combat.isUnderAttack()) {
			sendMessage("You can't rest right now.");
			return;
		}
		stopAction();
		walkingQueue.reset();
		runBeforeRest = isRunning();
		resting = true;
		restAnimationTicks = 80;
		// Build 592's orb script recognises varp 173 = 3 as normal resting.
		status.setVar(RUN_MODE_VAR, 3);
		super.playAnimation(new Animation(5713));
	}

	public void stopResting() {
		if (!resting)
			return;
		resting = false;
		status.setVar(RUN_MODE_VAR, runBeforeRest && getEnergy() > 0 ? 1 : 0);
		super.playAnimation(new Animation(5748));
	}

	@Override
	public void startAction(Action<?> action) {
		stopResting();
		super.startAction(action);
	}

	@Override
	public void stopAction() {
		stopResting();
		super.stopAction();
	}

	@Override
	public void playAnimation(Animation animation) {
		stopResting();
		super.playAnimation(animation);
	}

	@Override
	public void addHit(Hit hit) {
		if (!hit.isCosmetic())
			stopResting();
		super.addHit(hit);
	}

	public boolean hasRunEnergy() {
		return energy > 0;
	}

	/** Called once per movement tick, after the actual steps have been selected. */
	public void updateRunEnergy(boolean ran) {
		if (resting && (ran || combat.getTarget() != null || combat.isUnderAttack() || dead()))
			stopResting();
		if (resting && --restAnimationTicks <= 0) {
			// Refresh the sequence's loop count without letting a long rest expire.
			super.playAnimation(new Animation(5713));
			restAnimationTicks = 80;
		}
		int previousPercent = getEnergy();
		int agility = skillSet.getCurrentLevel(Skill.AGILITY);
		updateEnergy(ran ? energy - RunEnergy.drainPerTick(agility)
				: energy + (resting ? RunEnergy.restingRecoveryPerTick(agility)
						: RunEnergy.recoveryPerTick(agility)));
		if (getEnergy() != previousPercent)
			send(new EnergyMessage(getEnergy()));
	}

	@Override
	public void teleport(Position position) {
		stopResting();
		this.walkingQueue.reset();
		this.startPosition = this.position;
		this.position = position;
		this.teleporting = true;
	}

	@Override
	public void setHeadIcon(int headIcon) {
		super.setHeadIcon(headIcon);
		refreshAppearance();
	}

	@Override
	public void setPosition(Position position) {
		if (this.position != null && !this.position.equals(position))
			stopResting();
		this.position = position;
		if (display.getInterface(Interfaces.WORLD_MAP) != null)
			send(new GlobalIntVarMessage(674, position.toPackedInt()));
	}

	public void setLastKnownRegion(Position lastKnownRegion) {
		this.lastKnownRegion = lastKnownRegion;
		this.regionChanging = true;
	}

	public void refreshAppearance() {
		this.appearanceTicket = nextAppearanceTicket();
	}

	public void setEnergy(double energy) {
		updateEnergy(energy);
		this.send(new EnergyMessage(getEnergy()));
	}

	private void updateEnergy(double energy) {
		this.energy = Math.max(0, Math.min(100, energy));
		// Avoid keeping a near-zero/full value for an extra tick due to floating point error.
		if (this.energy < 1.0e-9)
			this.energy = 0;
		else if (this.energy > 100 - 1.0e-9)
			this.energy = 100;
		if (!hasRunEnergy()) {
			setRunning(false);
			walkingQueue.setRunningQueue(false);
		}
	}

	public void setBlackout(Blackout blackout) {
		this.blackout = blackout;
		send(blackout);
	}

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getRights() {
		return rights;
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	public GameSession getSession() {
		return session;
	}

	public void setSession(GameSession session) {
		this.session = session;
	}

	public void sendMessage(String text) {
		sendMessage(text, 0);// TODO
	}

	public void sendMessage(String text, int index) {
		send(new ServerMessage(text, index));
	}

	public boolean isRegionChanging() {
		return regionChanging;
	}

	public Position getLastKnownRegion() {
		return lastKnownRegion;
	}

	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	public List<Npc> getLocalNpcs() {
		return localNpcs;
	}

	public int getAppearanceTicket() {
		return appearanceTicket;
	}

	public int[] getAppearanceTickets() {
		return appearanceTickets;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	public int getEnergy() {
		return (int) energy;
	}

	public double getPreciseEnergy() {
		return energy;
	}

	public ChatMessage getChatMessage() {
		return chatMessage;
	}

	public void setChatMessage(ChatMessage message) {
		this.chatMessage = message;
	}

	public void setAppearance(Appearance appearance) {
		this.appearance = appearance;
		refreshAppearance();
	}

	/** Store a CS2-previewed style/colour without sending another appearance
	 * block. The 592 client scripts 2743/2745 update the local model themselves.
	 * Call refreshAppearance once the player confirms the design. */
	public void setCharacterCreationAppearance(Appearance appearance) {
		this.appearance = appearance;
	}

	public boolean isCharacterCreationRequired() {
		return characterCreationRequired;
	}

	public void setCharacterCreationRequired(boolean required) {
		characterCreationRequired = required;
	}

	public boolean isTutorialTabsRestricted() {
		return tutorialTabsRestricted;
	}

	public int getTutorialTabMask() {
		return tutorialTabMask;
	}

	public void setTutorialTabState(boolean restricted, int mask) {
		if ((mask & ~((1 << Tab.TAB_COUNT) - 1)) != 0)
			throw new IllegalArgumentException("Invalid tutorial tab mask");
		this.tutorialTabsRestricted = restricted;
		this.tutorialTabMask = mask;
	}

	public boolean isTutorialTabUnlocked(int slot) {
		return slot >= 0 && slot < Tab.TAB_COUNT
				&& (!tutorialTabsRestricted
					|| slot == Tab.SETTINGS
					|| (tutorialTabMask & (1 << slot)) != 0);
	}

	public boolean isControlsTutorialPending() {
		return controlsTutorialPending;
	}

	public void setControlsTutorialPending(boolean pending) {
		controlsTutorialPending = pending;
	}

	/**
	 * Fullscreen character setup changes the entire HUD root. Reopening an
	 * overlay in the same network flush can be lost when the 592 client
	 * rebuilds the HUD. Let at least two player-update cycles pass first.
	 */
	public void deferControlsTutorial(int ticks) {
		controlsTutorialOpeningDelay = Math.max(0, ticks);
	}

	public boolean isControlsTutorialOpenDeferred() {
		if (controlsTutorialOpeningDelay == 0)
			return false;
		controlsTutorialOpeningDelay--;
		return true;
	}

	public void recordControlsTutorialShown(long nowMillis) {
		controlsTutorialShownAt = nowMillis;
		// The first camera packet after the full-screen HUD transition is a
		// baseline, not evidence that the player pressed an arrow key.
		cameraPositionKnown = false;
	}

	public boolean canDismissControlsTutorial(long nowMillis) {
		return controlsTutorialShownAt > 0 && nowMillis - controlsTutorialShownAt >= 1200;
	}

	/**
	 * The camera packet describes an angle, not the keyboard event that caused it.
	 * Establish a baseline first so an initial camera synchronisation cannot
	 * dismiss the controls lesson without the player rotating the view.
	 */
	public boolean recordCameraPosition(int yaw, int pitch) {
		boolean moved = cameraPositionKnown && (lastCameraYaw != yaw || lastCameraPitch != pitch);
		lastCameraYaw = yaw;
		lastCameraPitch = pitch;
		cameraPositionKnown = true;
		return moved;
	}

	public int getCharacterCreationCategory() {
		return characterCreationCategory;
	}

	public void setCharacterCreationCategory(int category) {
		characterCreationCategory = category;
	}

	public boolean isChatUpdated() {
		return chatMessage != null;
	}

	public int getLocalX() {
		return position.getX() - ((lastKnownRegion.getChunkX() - (fov.getTiles() >> 4)) * 8);
	}

	public int getLocalY() {
		return position.getY() - ((lastKnownRegion.getChunkY() - (fov.getTiles() >> 4)) * 8);
	}

	public int[] getUpdateTickets() {
		return tickets;
	}

	public FieldOfView getFov() {
		return fov;
	}

	public void setFov(FieldOfView fov) {
		this.fov = fov;
	}

	public HeadsUpDisplay getDisplay() {
		return display;
	}

	public Status getStatus() {
		return status;
	}

	public InventoryContainer getInventories() {
		return inventories;
	}

	public Blackout getBlackout() {
		return blackout;
	}

	public void setStance(int stance) {
		this.stance = stance;
	}

	public int getStance() {
		return stance;
	}
}
