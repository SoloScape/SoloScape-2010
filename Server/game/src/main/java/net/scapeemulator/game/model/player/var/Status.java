package net.scapeemulator.game.model.player.var;

import java.util.ArrayDeque;
import java.util.Queue;

import net.scapeemulator.game.cache.VarBitDefinition;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.var.PVariable.Type;
import net.scapeemulator.game.msg.gameplay.StatusVariableMessage;
import net.scapeemulator.game.msg.gameplay.VarBitMessage;
import net.scapeemulator.game.msg.interf.util.GlobalIntVarMessage;

public final class Status {
	protected static final int[] max_values = new int[32];

	private final Player player;
	private final int[] waitingVars = new int[2000], currentVars = new int[2000];
	private final int[] intVars = new int[1100];
	private boolean loaded = false;
	/**
	 * Is this really necessary?
	 */
	private boolean processing = false;
	private final Queue<Integer> updatedVars = new ArrayDeque<>();
	private final Queue<VarBit> updatedVarBits = new ArrayDeque<>();

	public Status(Player player) {
		this.player = player;
	}

	public void setVar(int id, int status) {
		if (id < 0 || id > waitingVars.length)
			return;
		if (processing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		PVariable<?> var = PVariable.getVarById(id);
		if (var != null) {
			if (var.hasKey(status)) {
				waitingVars[id] = status;
				if (!updatedVars.contains(id)) {
					updatedVars.add(id);
				}
			}
		} else {
			waitingVars[id] = status;
			if (!updatedVars.contains(id)) {
				updatedVars.add(id);
			}
		}
	}

	public void defaults() {
		for (int i = 0; i < waitingVars.length; i++) {
			PVariable<?> var = PVariable.getVarById(i);
			if (var != null) {
				waitingVars[i] = var.getIndex(var.getDefaultValue());
				updatedVars.add(i);
			}
		}
	}

	public synchronized boolean refresh() {
		processing = true;
		if (loaded) {
			while (!updatedVarBits.isEmpty()) {
				VarBit varbit = updatedVarBits.poll();
				if (varbit != null) {
					refresh(varbit);
					player.send(new VarBitMessage(varbit.id, varbit.set));
					// ???
				}
			}
		} else {
			/**
			 * Should NEVER occur but idk, think this is good here
			 */
			while (!updatedVarBits.isEmpty()) {
				VarBit varbit = updatedVarBits.poll();
				VarBitVariable<?> variable = VarBitVariable.getVarById(varbit.id);
				if (variable != null && variable.getTrigger() != null) {
					variable.getTrigger().stateChanged(player, variable.getValue(varbit.set));
					player.sendMessage("[Status: VarBIT " + varbit.id + "(" + variable.getName() + "): Value set to "
							+ variable.getValue(varbit.set) + "]", 99);
				}
			}
		}
		while (!updatedVars.isEmpty()) {
			Integer id = updatedVars.poll();
			if (id != null) {
				PVariable<?> var = PVariable.getVarById(id);
				int value = waitingVars[id];
				if (var != null) {
					if (var.getTrigger() != null)
						var.getTrigger().stateChanged(player, var.getValue(value));
					// if (loaded)
					player.sendMessage("[Status: Variable " + id + "(" + var.getName() + "): Value set to "
							+ var.getValue(value) + "]", 99);
				}
				player.send(new StatusVariableMessage(id, value));
				currentVars[id] = value;
			}
		}
		processing = false;
		loaded = true;
		notify();
		return true;
	}

	public void setVarBit(int varBitId, int set) {
		if (processing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		updatedVarBits.add(new VarBit(varBitId, set));
		if (!loaded) {
			VarBitDefinition varbitDef = VarBitDefinition.forId(varBitId);
			if (varbitDef != null) {
				int varId = varbitDef.getVarId();
				int bottomBit = varbitDef.getBottomBit();
				int max = max_values[varbitDef.getTopBit() - bottomBit];
				if (0 > set || max < set) {
					set = 0;
				}
				max <<= bottomBit;
				waitingVars[varId] = max & set << bottomBit | ~max & waitingVars[varId];
				if (!updatedVars.contains(varId)) {
					updatedVars.add(varId);
				}
			}
		} else {
			updatedVarBits.add(new VarBit(varBitId, set));
		}
	}

	private void refresh(VarBit varbit) {
		VarBitVariable<?> variable = VarBitVariable.getVarById(varbit.id);
		VarBitDefinition varbitDef = VarBitDefinition.forId(varbit.id);
		if (varbitDef != null) {
			int set = varbit.set;
			int varId = varbitDef.getVarId();
			int bottomBit = varbitDef.getBottomBit();
			int topBit = varbitDef.getTopBit();
			int max = max_values[topBit - bottomBit];
			if (0 > set || max < set) {
				set = 0;
			}
			max <<= bottomBit;
			currentVars[varId] = max & set << bottomBit | ~max & currentVars[varId];
			if (variable != null) {
				player.sendMessage("[Status: VarBIT " + varbit.id + "(" + variable.getName() + "): Value set to "
						+ variable.getValue(varbit.set) + "]", 99);
				if (variable.getTrigger() != null) {
					variable.getTrigger().stateChanged(player, variable.getValue(set));
				}
			}
		}
	}

	/**
	 * Refreshes the given variable (re-triggers changes)
	 * 
	 * @param varName
	 *            the name of the variable
	 */
	public void refresh(String varName) {
		PVariable<?> var = PVariable.varPlayers.get(varName);
		int id = var.getId();
		if (id < 0 || id > waitingVars.length)
			return;
		if (processing) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		updatedVars.add(id);
	}

	/**
	 * DONE, TODO document
	 * 
	 * @param varName
	 * @param status
	 */
	public void setVarBitStatus(String varName, Object status) {
		VarBitVariable<?> var = VarBitVariable.varBits.get(varName);
		if (var != null) {
			int set = var.getIndex(status);
			if (processing) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			updatedVarBits.add(new VarBit(var.getId(), set));
		}
	}

	public void setStatus(String varName, Object status) {
		PVariable<?> var = PVariable.varPlayers.get(varName);
		int value = 0;
		if (var != null) {
			if (var.getType() == Type.VARBIT)
				throw new IllegalArgumentException("Can't set a VARBIT parent status!");
			int id = var.getId();
			if (id < 0 || id > waitingVars.length)
				return;
			if (processing) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			value = var.getIndex(status);
			waitingVars[id] = value;
			updatedVars.add(id);
		}
	}

	public void setVarC(int id, int value) {
		if (id < 0 || id > intVars.length)
			return;
		intVars[id] = value;
		Trigger<Integer> trigger = CVariable.getTrigger(id);
		if (trigger != null)
			trigger.stateChanged(player, value);
		player.send(new GlobalIntVarMessage(id, value));
	}

	public int getVarC(int id) {
		if (id < 0 || id > intVars.length)
			return 0;
		return intVars[id];
	}

	public void toggle(String varName) {
		setStatus(varName, !(Boolean) getStatus(varName));
	}

	public void toggleVarBit(String varName) {
		setVarBitStatus(varName, !(Boolean) getStatus(varName));
	}

	/**
	 * USE ONLY FOR SIMPLE INTEGER VARIABLES
	 * 
	 * @param varName
	 * @param count
	 */
	public void increment(String varName, int count) {
		PVariable<?> var = PVariable.varPlayers.get(varName);
		if (var != null) {
			if (var.getType() == Type.VARBIT)
				throw new IllegalArgumentException("Can't set a VARBIT parent status!");
			int id = var.getId();
			if (id < 0 || id > waitingVars.length)
				return;
			if (processing) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			waitingVars[id] = var.getIndex(waitingVars[id] + count);
			updatedVars.add(id);
		}
	}

	/**
	 * USE ONLY FOR SIMPLE INTEGER VARIABLES
	 * 
	 * @param varName
	 * @param count
	 */
	public void decrement(String varName, int count) {
		PVariable<?> var = PVariable.varPlayers.get(varName);
		if (var != null) {
			if (var.getType() == Type.VARBIT)
				throw new IllegalArgumentException("Can't set a VARBIT parent status!");
			int id = var.getId();
			if (id < 0 || id > waitingVars.length)
				return;
			if (processing) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			waitingVars[id] = var.getIndex(waitingVars[id] - count);
			updatedVars.add(id);
		}
	}

	public Object getVarBitState(String varName) {
		VarBitVariable<?> var = VarBitVariable.varBits.get(varName);
		if (var != null) {
			int status = getVarBit(var.getId());
			return var.getValue(var.getIndex(status));
		}
		return null;
	}

	public int getVarBit(int varBitId) {
		VarBitDefinition varbitDef = VarBitDefinition.forId(varBitId);
		int var = varbitDef.getVarId();
		int bottom = varbitDef.getBottomBit();
		int top = varbitDef.getTopBit();
		int max = max_values[top - bottom];
		return currentVars[var] >> bottom & max;
	}

	public Object getStatus(String varName) {
		PVariable<?> var = PVariable.varPlayers.get(varName);
		if (var == null || var.getType() == Type.VARBIT)
			return null;
		return var.getValue(currentVars[var.getId()]);
	}

	public Object getWaitingStatus(String varName) {
		PVariable<?> var = PVariable.varPlayers.get(varName);
		if (var == null || var.getType() == Type.VARBIT)
			return null;
		return var.getValue(waitingVars[var.getId()]);
	}

	public int getWaitingVar(int varId) {
		if (varId < 0 || varId > waitingVars.length)
			return 0;
		return waitingVars[varId];
	}

	public int getCurrentVar(int varId) {
		if (varId < 0 || varId > currentVars.length)
			return 0;
		return currentVars[varId];
	}

	public boolean isLoaded() {
		return loaded;
	}

	static {
		int i = 2;
		for (int j = 0; j < 32; j++) {
			max_values[j] = i - 1;
			i += i;
		}
	}
}
