package com.radixdlt.client.examples.chess;

import com.radixdlt.client.core.crypto.ECKeyPair;
import com.radixdlt.client.core.crypto.ECPublicKey;
import com.radixdlt.client.core.crypto.RadixECKeyPairs;

public class KeyUtils {
	private static RadixECKeyPairs radixECKeyPairs;

	public static ECKeyPair fromSeed(String seed) {
		if (radixECKeyPairs == null) {
			radixECKeyPairs = RadixECKeyPairs.newInstance();
		}

		return radixECKeyPairs
			.generateKeyPairFromSeed(seed.getBytes());
	}
}
