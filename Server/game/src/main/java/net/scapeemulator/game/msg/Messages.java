package net.scapeemulator.game.msg;

import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.handler.MessageDispatcher;
import net.scapeemulator.game.GameServer;
import net.scapeemulator.game.command.CommandDispatcher;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.chunk.ChunkUpdateMessageEncoder;
import net.scapeemulator.game.msg.entity.ExamineMessage.ExamineType;
import net.scapeemulator.game.model.hud.interf.action.*;
import net.scapeemulator.game.msg.anticheat.*;
import net.scapeemulator.game.msg.area.*;
import net.scapeemulator.game.msg.audio.*;
import net.scapeemulator.game.msg.entity.*;
import net.scapeemulator.game.msg.gameplay.*;
import net.scapeemulator.game.msg.interf.*;
import net.scapeemulator.game.msg.interf.child.*;
import net.scapeemulator.game.msg.interf.util.*;
import net.scapeemulator.game.msg.inventory.*;
import net.scapeemulator.game.msg.util.*;
import net.scapeemulator.game.util.LandscapeKeyTable;

public class Messages {
	public static final int[] SIZES = new int[256];

	static {
		for (int i = 0; i < 256; i++) {
			SIZES[i] = -3;
		}
		SIZES[0] = -1;
		SIZES[1] = 2;
		SIZES[2] = -1;
		SIZES[3] = -1;
		SIZES[4] = -1;
		SIZES[5] = 5;
		SIZES[6] = 8;
		SIZES[7] = 7;
		SIZES[8] = 8;
		SIZES[9] = -1;
		SIZES[10] = 7;
		SIZES[11] = 7;
		SIZES[12] = 4;
		SIZES[13] = 3;
		SIZES[14] = 7;
		SIZES[15] = -1;
		SIZES[16] = 15;
		SIZES[17] = 0;
		SIZES[18] = 6;
		SIZES[19] = 2;
		SIZES[20] = 8;
		SIZES[21] = -1;
		SIZES[22] = 4;
		SIZES[23] = 3;
		SIZES[24] = -1;
		SIZES[25] = 4;
		SIZES[26] = 7;
		SIZES[27] = -1;
		SIZES[28] = 8;
		SIZES[29] = 7;
		SIZES[30] = 3;
		SIZES[31] = 3;
		SIZES[32] = -1;
		SIZES[33] = 11;
		SIZES[34] = 11;
		SIZES[35] = 3;
		SIZES[36] = 3;
		SIZES[37] = 15;
		SIZES[38] = 8;
		SIZES[39] = 7;
		SIZES[40] = 3;
		SIZES[41] = 3;
		SIZES[42] = -1;
		SIZES[43] = 2;
		SIZES[44] = -1;
		SIZES[45] = 3;
		SIZES[46] = 8;
		SIZES[47] = 3;
		SIZES[48] = 1;
		SIZES[49] = 6;
		SIZES[50] = -1;
		SIZES[51] = 4;
		SIZES[52] = -1;
		SIZES[53] = -1;
		SIZES[54] = 12;
		SIZES[55] = 16;
		SIZES[56] = -1;
		SIZES[57] = 4;
		SIZES[58] = 6;// Window Mode.
		SIZES[59] = 18;
		SIZES[60] = 3;
		SIZES[61] = 2;
		SIZES[62] = 8;
		SIZES[63] = -1;
		SIZES[64] = 8;
		SIZES[65] = 3;
		SIZES[66] = 8;
		SIZES[67] = 7;
		SIZES[68] = 3;
		SIZES[69] = 0;
		SIZES[70] = 8;
		SIZES[71] = 2;
		SIZES[72] = -1;
		SIZES[73] = 2;
		SIZES[74] = 0;// Ping
		SIZES[75] = 16;
		SIZES[76] = 3;
		SIZES[77] = 7;
		SIZES[78] = -1;// Commands
		SIZES[79] = -1;
		SIZES[80] = 7;
		SIZES[81] = 4;

	}

	public static void init(GameServer server) {
		init(server.getCodecRepository(), server.getLandscapeKeyTable());
		init(server.getMessageDispatcher(), server.getCommandDispatcher());
	}

	private static void init(CodecRepository repository, LandscapeKeyTable table) {
		/* decoders */
		repository.bind(new PingMessageDecoder());
		// repository.bind(new IdleLogoutMessageDecoder());
		repository.bind(new WalkMessageDecoder(WalkMessage.HUD_WALK));
		repository.bind(new WalkMessageDecoder(WalkMessage.MINIMAP_WALK));

		// repository.bind(new ChatMessageDecoder());
		repository.bind(new CommandMessageDecoder());
		repository.bind(new DisplayMessageDecoder());
		// repository.bind(new RemoveItemMessageDecoder());
		// repository.bind(new RegionChangedMessageDecoder());
		repository.bind(new ClickMessageDecoder());
		repository.bind(new FocusMessageDecoder());
		repository.bind(new CameraMessageDecoder());
		// repository.bind(new FlagsMessageDecoder());

		// Examines
		repository.bind(new ExamineDecoder(ExamineType.OBJECT));// boo yeah

		// Music
		repository.bind(new TrackEndDecoder());// boo yeah

		repository.bind(new ObjectOptionOneMessageDecoder());// TODO: Generize
		repository.bind(new ObjectOptionTwoMessageDecoder());

		repository.bind(new SequenceNumberMessageDecoder());

		repository.bind(new InterfaceClosedMessageDecoder());
		repository.bind(new ChildClickDecoder());
		repository.bind(new ChildInterfaceDragDecoder());
		for (int opt = 1; opt <= 10; opt++) {
			repository.bind(new ChildOptionClickMessageDecoder(opt));
		}

		/* encoders */
		repository.bind(new StaticAreaMessageEncoder(table));
		repository.bind(new BuiltAreaMessageEncoder(table));
		repository.bind(new ChunkUpdateMessageEncoder());
		repository.bind(new PlayerUpdateMessageEncoder());

		repository.bind(new InterfaceRootMessageEncoder());
		repository.bind(new InterfaceOpenMessageEncoder());
		repository.bind(new InterfaceMoveMessageEncoder());
		repository.bind(new InterfaceCloseMessageEncoder());

		repository.bind(new InventoryMessageEncoder());
		repository.bind(new InventorySlottedItemMessageEncoder());
		repository.bind(new InventoryClearMessageEncoder());

		repository.bind(new ChildInterfaceSettingsEncoder());

		repository.bind(new StatusVariableMessageEncoder());
		repository.bind(new VarBitMessageEncoder());
		repository.bind(new SkillMessageEncoder());
		repository.bind(new BlackoutEncoder());

		repository.bind(new ServerMessageEncoder());
		repository.bind(new LogoutMessageEncoder());
		// repository.bind(new InterfaceVisibleMessageEncoder());
		repository.bind(new InterfaceTextMessageEncoder());
		repository.bind(new InterfaceSpriteMessageEncoder());
		repository.bind(new PlayerOnInterfaceMessageEncoder());

		repository.bind(new EnergyMessageEncoder());
		repository.bind(new GlobalIntVarMessageEncoder());
		repository.bind(new GlobalStringVarMessageEncoder());
		repository.bind(new ScriptMessageEncoder());
		// repository.bind(new ResetMinimapFlagMessageEncoder());
		repository.bind(new NpcUpdateMessageEncoder());

		// Audio packets.
		repository.bind(new MusicTrackEncoder());
		repository.bind(new SoundEffectEncoder());
		repository.bind(new JingleEncoder());
	}

	private static void init(MessageDispatcher<Player> disp, CommandDispatcher commands) {
		disp.bind(PingMessage.class, new PingMessageHandler());
		disp.bind(LogoutMessage.class, new LogoutMessageHandler());
		disp.bind(WalkMessage.class, new WalkMessageHandler());
		disp.bind(ChatMessage.class, new ChatMessageHandler());
		disp.bind(CommandMessage.class, new CommandMessageHandler(commands));
		disp.bind(DisplayMessage.class, new DisplayMessageHandler());
		disp.bind(AreaChangeMessage.class, new AreaChangeMessageHandler());
		disp.bind(ClickMessage.class, new ClickMessageHandler());
		disp.bind(FocusMessage.class, new FocusMessageHandler());
		disp.bind(CameraMessage.class, new CameraMessageHandler());
		disp.bind(FlagsMessage.class, new FlagsMessageHandler());
		disp.bind(SequenceNumberMessage.class, new SequenceNumberMessageHandler());
		disp.bind(ObjectClickMessage.class, new ObjectClickMessageHandler());
		disp.bind(ExamineMessage.class, new ExamineHandler());

		disp.bind(InterfaceAction.class, new InterfaceActionHandler<InterfaceAction>());
		disp.bind(ChildInterfaceClickAction.class, new InterfaceActionHandler<ChildInterfaceClickAction>());
		disp.bind(ChildInterfaceDragAction.class, new InterfaceActionHandler<ChildInterfaceDragAction>());
	}
}
