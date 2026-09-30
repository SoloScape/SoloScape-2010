package net.scapeemulator.api.message.codec.impl;

import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.message.codec.CodecRepository;
import net.scapeemulator.api.message.codec.MessageEncoder;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OutgoingMessageEncoder extends MessageToMessageEncoder<Message> {
	private final static Logger logger = LoggerFactory.getLogger(OutgoingMessageEncoder.class);
	private final CodecRepository codecs;

	public OutgoingMessageEncoder(CodecRepository codecs) {
		this.codecs = codecs;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void encode(ChannelHandlerContext ctx, Message message, MessageBuf<Object> out) throws IOException {
		try {
			MessageEncoder<Message> encoder = (MessageEncoder<Message>) codecs.get(message.getClass());
			if (encoder != null)
				out.add(encoder.encode(ctx.alloc(), message));
			else
				logger.warn("Can't encode message " + message.getClass().getSimpleName() + "! no encoder!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
