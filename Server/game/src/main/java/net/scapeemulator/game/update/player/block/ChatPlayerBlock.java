package net.scapeemulator.game.update.player.block;

import net.scapeemulator.api.net.packet.DataTransformation;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.PacketBuilder;
import net.scapeemulator.game.model.player.Player;
import net.scapeemulator.game.msg.entity.PlayerUpdateMessage;
import net.scapeemulator.game.msg.gameplay.ChatMessage;
import net.scapeemulator.game.update.player.PlayerBlock;
import net.scapeemulator.util.ChatUtils;

import java.util.Arrays;

public final class ChatPlayerBlock extends PlayerBlock {

	private final ChatMessage chatMessage;
	private final int rights;

	public ChatPlayerBlock(Player player) {
		super(0x80);
		this.chatMessage = player.getChatMessage();
		this.rights = player.getRights();
	}

	@Override
	public void encode(PlayerUpdateMessage message, PacketBuilder builder) {
		byte[] bytes = new byte[256];
		int size = ChatUtils.pack(chatMessage.getText(), bytes);

		builder.put(DataType.SHORT, DataTransformation.ADD, (chatMessage.getColor() << 8) | chatMessage.getEffects());
		builder.put(DataType.BYTE, DataTransformation.ADD, rights);
		builder.put(DataType.BYTE, DataTransformation.ADD, size);
		builder.putBytes(Arrays.copyOf(bytes, size));
	}
}
