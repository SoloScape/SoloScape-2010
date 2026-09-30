package net.scapeemulator.xtalk.login;

public class XTalkLoginHandshake extends XTalkMessage {
	private static final int LOGIN = 128;

	public int getOpcode() {
		return LOGIN;
	}
}
