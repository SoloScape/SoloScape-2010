package net.scapeemulator.game;

import net.scapeemulator.game.GameServer;
import net.scapeemulator.game.Plugin;
import net.scapeemulator.game.command.*;
import net.scapeemulator.game.model.inventory.Inventory.StackMode;
import net.scapeemulator.game.model.inventory.InventoryAppearanceListener;
import net.scapeemulator.game.model.hud.trigger.ItemInformationTrigger;
import net.scapeemulator.game.model.hud.trigger.PrayerTrigger;
import net.scapeemulator.game.model.hud.trigger.RetaliateTrigger;
import net.scapeemulator.game.model.hud.trigger.SpecialTrigger;
import net.scapeemulator.game.model.hud.trigger.SplitChatTrigger;
import net.scapeemulator.game.model.hud.util.MusicTab;
import net.scapeemulator.game.model.inventory.Inventory;
import net.scapeemulator.game.model.inventory.InventoryInfo;
import net.scapeemulator.game.model.mob.combat.SpecialAttack;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.model.player.combat.*;
import net.scapeemulator.game.model.player.var.CVariable;
import net.scapeemulator.game.model.player.var.PVariable;
import net.scapeemulator.game.model.player.var.PVariable.Builder;
import net.scapeemulator.game.model.player.var.PVariable.Type;
import net.scapeemulator.game.model.tele.TeleportCommand;
import net.scapeemulator.game.msg.audio.TrackEndMessageHandler;
import net.scapeemulator.game.msg.audio.TrackEndMessage;
import net.scapeemulator.game.model.player.var.SimpleIntegerVariable;
import net.scapeemulator.game.model.player.var.Trigger;

public class DefaultGameLogic extends Plugin {

	@Override
	public void register(GameServer gameServer) {
		createCoreVars();

		gameServer.getMessageDispatcher().bind(TrackEndMessage.class, new TrackEndMessageHandler());

		gameServer.getCommandDispatcher().bind(new TeleportCommand());
		gameServer.getCommandDispatcher().bind(new BankCommand());
		gameServer.getCommandDispatcher().bind(new Worldmap());
		gameServer.getCommandDispatcher().bind(new MusicUnlock());
		gameServer.getCommandDispatcher().bind(new Orientate());
		gameServer.getCommandDispatcher().bind(new IkodCommand());
		gameServer.getCommandDispatcher().bind(new NPCTransform());
		gameServer.getCommandDispatcher().bind(new MakeInterface());
		gameServer.getCommandDispatcher().bind(new ShopCommand());

		// TODO: Automatically aligning inventories, keep_empty/discard_empty
		InventoryInfo info;
		info = new InventoryInfo(Inventory.BACKPACK, "inventory", 149, 0, 28, StackMode.STACKABLE_ONLY);
		Inventory.addDetails(info);
		
		info = new InventoryInfo(Inventory.EQUIPMENT, "equipment", 387, 28, 14, StackMode.STACKABLE_ONLY,
				new InventoryAppearanceListener());
		Inventory.addDetails(info);
		
		info = new InventoryInfo(Inventory.BANK, "bank", 762, 87, 496, StackMode.ALWAYS);
		Inventory.addDetails(info);
		
		info = new InventoryInfo(Inventory.GENERAL_STORE_FREEBIES, "Samples", 620, 26, 496, StackMode.ALWAYS);
		info.disableClearEmpty();
		Inventory.addDetails(info);

		// TODO: Load from item definitions
		// TODO: Lent items?
		SpecialAttack.register(SpecialAttack.Type.ITEM, 4151, new EnergyDrain());
		SpecialAttack.register(SpecialAttack.Type.ITEM, 11694, new Judgement());
		SpecialAttack.register(SpecialAttack.Type.ITEM, 11696, new WarStrike());
		SpecialAttack.register(SpecialAttack.Type.ITEM, 11698, new HealingBlade());
		SpecialAttack.register(SpecialAttack.Type.ITEM, 14484, new SliceAndDice());
		// TODO: Ice cleave
		
		CVariable.registerTrigger(741, new ItemInformationTrigger());
		
		//TODO: Shop triggers
	}

	private void createCoreVars() {
		Builder<Boolean> varBuilder = new PVariable.Builder<Boolean>("autoretaliate");
		varBuilder.value(true).value(false).setDefault(true).setTrigger(new RetaliateTrigger()).id(172).persist()
				.type(Type.VARP).register();

		varBuilder = new PVariable.Builder<Boolean>("chateffects");
		varBuilder.value(true).value(false).setDefault(true).id(171).persist().type(Type.VARP).register();

		varBuilder = new PVariable.Builder<Boolean>("mousebuttons");
		varBuilder.value(true).value(false).setDefault(true).id(170).persist().type(Type.VARP).register();

		varBuilder = new PVariable.Builder<Boolean>("accept_aid");
		varBuilder.value(false).value(true).setDefault(false).id(427).persist().type(Type.VARP).register();

		varBuilder = new PVariable.Builder<Boolean>("split_private_chat");
		varBuilder.value(false).value(true).setDefault(false).id(287).persist().type(Type.VARP);
		varBuilder.setTrigger(new SplitChatTrigger());
		varBuilder.register();

		varBuilder = new PVariable.Builder<Boolean>("using_special");
		varBuilder.value(false).value(true).setDefault(false).id(301).type(Type.VARP);
		varBuilder.setTrigger(new SpecialTrigger());
		varBuilder.register();

		varBuilder = new PVariable.Builder<Boolean>("bank_withdraw_note");
		varBuilder.value(false).value(true).setDefault(false).id(115).type(Type.VARP);
		varBuilder.register();

		varBuilder = new PVariable.Builder<Boolean>("bank_insert");
		varBuilder.value(false).value(true).setDefault(false).id(304).type(Type.VARP);
		varBuilder.register();

		Builder<Integer> intvarBuilder = new PVariable.Builder<Integer>("attack_style");
		intvarBuilder.setDefault(0).value(0).value(1).value(2).value(3).id(43).type(Type.VARP);
		intvarBuilder.setTrigger(new Trigger<Integer>() {

			@Override
			public void trigger(Player player, Integer current) {
				player.getCombat().setCurrentStyle(current);
			}
		}).register();

		SimpleIntegerVariable.Builder builder = new SimpleIntegerVariable.Builder("special_attack_power");
		builder.minimumValue(0).maximumValue(1000);
		builder.id(300).type(Type.VARP).setDefault(1000).persist().register();

		builder = new SimpleIntegerVariable.Builder("bank_x").minimumValue(0);
		builder.id(1249).type(Type.VARP).setDefault(50).persist().register();

		builder = new SimpleIntegerVariable.Builder("prayer");
		builder.id(1395).type(Type.VARP).setTrigger(new PrayerTrigger()).persist().register();

		MusicTab.buildVariables();
	}
}
