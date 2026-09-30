package net.scapeemulator.game.msg.entity;

import net.scapeemulator.api.message.Message;
import net.scapeemulator.api.net.packet.DataOrder;
import net.scapeemulator.api.net.packet.DataTransformation;

public class ExamineMessage implements Message {
	private final ExamineType type;
	private final int id;

	public ExamineMessage(ExamineType type, int id) {
		this.type = type;
		this.id = id;
	}

	public ExamineType getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public static enum ExamineType {
		OBJECT(73);
		private final int opcode;
		private final DataTransformation transf;
		private final DataOrder order;

		private ExamineType(int opcode) {
			this(opcode, DataOrder.BIG, DataTransformation.NONE);
		}

		private ExamineType(int opcode, DataOrder order, DataTransformation transf) {
			this.opcode = opcode;
			this.order = order;
			this.transf = transf;
		}

		public int getOpcode() {
			return opcode;
		}

		public DataTransformation getTransf() {
			return transf;
		}

		public DataOrder getOrder() {
			return order;
		}
	}
}
