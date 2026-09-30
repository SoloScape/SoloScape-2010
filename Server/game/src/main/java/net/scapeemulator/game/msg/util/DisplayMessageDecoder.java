package net.scapeemulator.game.msg.util;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.game.model.hud.DisplayMode;

import java.io.IOException;

public final class DisplayMessageDecoder extends MessageDecoder<DisplayMessage> {

	public DisplayMessageDecoder() {
		super(58);
	}

	@Override
	public DisplayMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int mode = (int) reader.getUnsigned(DataType.BYTE);
		int width = (int) reader.getUnsigned(DataType.SHORT);
		int height = (int) reader.getUnsigned(DataType.SHORT);
		reader.getUnsigned(DataType.BYTE); // TODO identify this
		DisplayMode displ = DisplayMode.FIXED;
		if (mode >= 0 && mode < DisplayMode.values().length) {
			displ = DisplayMode.values()[mode];
		}
		return new DisplayMessage(displ, width, height);
	}

}
