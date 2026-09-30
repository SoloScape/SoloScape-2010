package net.scapeemulator.util.crypto;

import net.scapeemulator.api.crypto.StreamCipher;

public final class ZeroStreamCipher implements StreamCipher {

	@Override
	public int nextInt() {
		return 0;
	}

}
