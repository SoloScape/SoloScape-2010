package net.scapeemulator.game.msg.interf;

import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;
import net.scapeemulator.game.model.hud.interf.action.InterfaceAction;

import java.io.IOException;

public final class InterfaceClosedMessageDecoder extends MessageDecoder<InterfaceAction> {

	public InterfaceClosedMessageDecoder() {
		super(69);
	}

	@Override
	public InterfaceAction decode(Packet frame) throws IOException {
		return InterfaceAction.CLOSE_ACTION;
	}
}
