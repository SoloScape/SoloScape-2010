package net.scapeemulator.api.message.codec.impl;

import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.codec.MessageDecoder;
import net.scapeemulator.api.net.packet.Packet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class IncomingMessageDecoder extends MessageToMessageDecoder<Packet> {
	private static final Logger logger = LoggerFactory.getLogger(IncomingMessageDecoder.class);

	private final CodecRepository codecs;

	public IncomingMessageDecoder(CodecRepository codecs) {
		this.codecs = codecs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decode(ChannelHandlerContext ctx, Packet packet, MessageBuf<Object> out) throws IOException {
		MessageDecoder<Message> decoder = (MessageDecoder<Message>) codecs.get(packet.getOpcode());

		if (decoder == null) {
			logger.warn("No decoder for packet id " + packet.getOpcode() + ".");
			return;
		}
		out.add(decoder.decode(packet));
	}
}
