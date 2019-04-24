package org.radix.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

class SHAHasher implements Hasher {
	// Note that default provide around 20-25% faster than Bouncy Castle.
	// See jmh/org.radix.benchmark.HashBenchmark
	private final MessageDigest hash256DigesterInner = getDigester("SHA-512", null);
	private final MessageDigest hash256DigesterOuter = getDigester("SHA-256", null);
	private final MessageDigest hash512Digester      = getDigester("SHA-512", null);

	SHAHasher() {
	}

	@Override
	public byte[] hash256Raw(byte[] data) {
		synchronized (hash256DigesterOuter) {
			hash256DigesterOuter.reset();
			hash256DigesterOuter.update(data);
			return hash256DigesterOuter.digest();
		}
	}

	@Override
	public byte[] hash256(byte[] data) {
		return hash256(data, 0, data.length);
	}

	@Override
	public byte[] hash256(byte[] data, int offset, int length) {
		// Here we use SHA-256(SHA-512(data)) to avoid length-extension attack
		synchronized (hash256DigesterOuter) {
			hash256DigesterOuter.reset();
			hash256DigesterInner.reset();
			hash256DigesterInner.update(data, offset, length);
			return hash256DigesterOuter.digest(hash256DigesterInner.digest());
		}
	}

	@Override
	public byte[] hash256(byte[] data0, byte[] data1) {
		// Here we use SHA-256(SHA-512(data0 || data1)) to avoid length-extension attack
		synchronized (hash256DigesterOuter) {
			hash256DigesterInner.reset();
			hash256DigesterOuter.reset();
			hash256DigesterInner.update(data0);
			return hash256DigesterOuter.digest(hash256DigesterInner.digest(data1));
		}
	}

	@Override
	public byte[] hash512(byte[] data, int offset, int length) {
		// Here we use SHA-512(SHA-512(data0 || data1)) to avoid length-extension attack
		synchronized (hash512Digester) {
			hash512Digester.reset();
			hash512Digester.update(data, offset, length);
			return hash512Digester.digest(hash512Digester.digest());
		}
	}

	private static MessageDigest getDigester(String algorithm, String provider) {
		try {
			return (provider == null)
				? MessageDigest.getInstance(algorithm)
				: MessageDigest.getInstance(algorithm, provider);
		} catch (NoSuchProviderException e) {
			throw new IllegalArgumentException("No such provider for: " + algorithm, e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("No such algorithm: " + algorithm, e);
		}
	}
}
