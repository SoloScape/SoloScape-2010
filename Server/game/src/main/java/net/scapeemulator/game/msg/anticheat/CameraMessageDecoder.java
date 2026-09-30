package net.scapeemulator.game.msg.anticheat;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.*;

import java.io.IOException;

public final class CameraMessageDecoder extends MessageDecoder<CameraMessage> {

	public CameraMessageDecoder() {
		super(51);
	}

	// TODO Order might be invalid.
	@Override
	public CameraMessage decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int pitch = (int) reader.getUnsigned(DataType.SHORT, DataTransformation.ADD);
		int yaw = (int) reader.getUnsigned(DataType.SHORT, DataOrder.LITTLE, DataTransformation.ADD);
		return new CameraMessage(yaw, pitch);
	}
}
