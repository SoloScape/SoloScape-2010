package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;

public final class ChildClickDecoder extends MessageDecoder<ChildInterfaceClickAction> {

	public ChildClickDecoder() {
		super(18);
	}

	@Override
	public ChildInterfaceClickAction decode(Packet frame) {
		PacketReader reader = new PacketReader(frame);
		int button = (int) reader.getSigned(DataType.INT);
		int id = (button >> 16) & 0xFFFF;
		int child = button & 0xFFFF;
		return new ChildInterfaceClickAction(0, id, child, -1, -1);
	}
}
