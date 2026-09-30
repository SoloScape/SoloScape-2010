package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.*;

import java.io.IOException;

public final class ClickMessageDecoder extends MessageDecoder<ClickMessage> {

	public ClickMessageDecoder() {
		super(49);
	}

	@Override
	public ClickMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int pos = (int) reader.getUnsigned(DataType.INT);
		int flags = (int) reader.getUnsigned(DataType.SHORT, DataTransformation.ADD);

		int time = flags & 0x7fff;
		boolean rightClick = ((flags >> 15) & 0x1) != 0;

		int x = pos & 0xffff;
		int y = (pos >> 16) & 0xffff;

		return new ClickMessage(time, x, y, rightClick);
	}
}
