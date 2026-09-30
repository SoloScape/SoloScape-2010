package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataType;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.api.net.packet.PacketReader;
import net.scapeemulator.game.model.hud.interf.action.ChildInterfaceClickAction;

import java.io.IOException;

public final class ChildOptionClickMessageDecoder extends MessageDecoder<ChildInterfaceClickAction> {
	private final int option;

	public ChildOptionClickMessageDecoder(int option) {
		super(getId(option));
		this.option = option;
	}

	private static final int getId(int option) {
		switch (option) {
		case 1:
			return 6;
		case 2:
			return 38;
		case 3:
			return 62;
		case 4:
			return 46;
		case 5:
			return 64;
		case 6:
			return 66;
		case 7:
			return 8;
		case 8:
			return 28;
		case 9:
			return 20;
		case 10:
			return 70;
		default:
			throw new IllegalArgumentException("Invalid option specified!");
		}
	}

	@Override
	public ChildInterfaceClickAction decode(Packet frame) throws IOException {
		PacketReader reader = new PacketReader(frame);
		int button = (int) reader.getSigned(DataType.INT);
		int id = (button >> 16) & 0xFFFF;
		int child = button & 0xFFFF;
		int slot = (int) reader.getUnsigned(DataType.SHORT, DataOrder.LITTLE);
		int media = (int) reader.getUnsigned(DataType.SHORT, DataOrder.LITTLE);
		if (slot == 65535)
			slot = -1;
		if (media == 65535)
			media = -1;
		return new ChildInterfaceClickAction(option, id, child, slot, media);
	}
}
