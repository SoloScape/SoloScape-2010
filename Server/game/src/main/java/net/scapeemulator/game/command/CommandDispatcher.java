package net.scapeemulator.game.command;

import java.util.HashMap;
import java.util.Map;

import net.scapeemulator.game.model.player.Player;

public final class CommandDispatcher {
	private final Map<String, CommandHandler> handlers = new HashMap<>();

	public CommandDispatcher() {
		bind(new TeleportCommandHandler());
		bind(new ItemCommandHandler());
		bind(new StatusVariableCommandHandler());
		bind(new EmptyCommandHandler());
		bind(new PositionCommandHandler());
		bind(new MasterCommandHandler());
		bind(new GetItemData());
		bind(new AudioCommand());
		bind(new CombatTest());
		bind(new GetVarPlrCommandHandler());
		bind(new VarClientCommandHandler());
		bind(new VarStringCommandHandler());
		bind(new VarBitCommandHandler());
		bind(new TestScriptCommand());
	}

	public void bind(CommandHandler handler) {
		handlers.put(handler.getName(), handler);
	}

	public void handle(Player player, String command) {
		String[] parts = command.split(" ");

		String name = parts[0];
		String[] arguments = new String[parts.length - 1];
		System.arraycopy(parts, 1, arguments, 0, arguments.length);

		CommandHandler handler = handlers.get(name);
		if (handler != null)
			handler.handle(player, arguments);
	}
}
