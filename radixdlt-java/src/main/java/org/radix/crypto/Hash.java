package org.radix.crypto;

import com.google.common.primitives.UnsignedBytes;
import org.radix.common.ID.EUID;
import org.radix.utils.primitives.Bytes;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;

public final class Hash implements Comparable<org.radix.crypto.Hash> {

	private static final Comparator<byte[]> COMPARATOR = UnsignedBytes.lexicographicalComparator();
	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
	private static Hasher hasher = new SHAHasher();

	public static final int BYTES = 32;
	public static final int BITS = BYTES * Byte.SIZE;
	public static final org.radix.crypto.Hash ZERO_HASH = new org.radix.crypto.Hash(new byte[BYTES]);

	public static org.radix.crypto.Hash random() {
		byte[] randomBytes = new byte[BYTES];

		SECURE_RANDOM.nextBytes(randomBytes);

		return new org.radix.crypto.Hash(hasher.hash256(randomBytes));
	}

	public static byte[] sha256Raw(byte[] data) {
		return hasher.hash256Raw(data);
	}

	public static byte[] sha256(byte[] data) {
		return hasher.hash256(data, 0, data.length);
	}

	public static byte[] sha256(byte[] data, int offset, int length) {
		return hasher.hash256(data, offset, length);
	}

	public static byte[] sha512(byte[] data) {
		return hasher.hash512(data, 0, data.length);
	}

	public static byte[] sha512(byte[] data, int offset, int length) {
		return hasher.hash512(data, offset, length);
	}

	public static byte[] sha256(byte[] data0, byte[] data1) {
		return hasher.hash256(data0, data1);
	}

	private final byte[] bytes;
	private EUID id;

	// Hashcode caching
	private boolean hashCodeComputed = false;
	private int hashCode;

	public Hash(byte[] hash) {
		if (hash.length != BYTES) {
			throw new IllegalArgumentException("Digest length must be " + BYTES + " bytes for Hash, was: " + hash.length);
		}

		this.bytes = hash;
	}

	public Hash(byte[] hash, int offset, int length) {
		if (length != BYTES) {
			throw new IllegalArgumentException("Digest length must be " + BYTES + " bytes for Hash, was " + length);
		}

		this.bytes = new byte[BYTES];
		System.arraycopy(hash, offset, this.bytes, 0, BYTES);
	}

	public Hash(String hex) {
		if (hex.length() != (BYTES * 2)) {
			throw new IllegalArgumentException("Digest length must be 64 hex characters for Hash, was " + hex.length());
		}

		this.bytes = Bytes.fromHexString(hex);
	}

	public byte[] toByteArray() {
		// FIXME MPS: Do we need to make this immutable
		//return bytes.clone();
		return bytes;
	}

	@Override
	public int compareTo(org.radix.crypto.Hash object) {
		return COMPARATOR.compare(this.bytes, object.bytes);
	}

	@Override
	public String toString() {
		return Bytes.toHexString(this.bytes);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof org.radix.crypto.Hash) {
			org.radix.crypto.Hash other = (org.radix.crypto.Hash) o;
			return Arrays.equals(this.bytes, other.bytes);
		}

		return false;
	}

	@Override
	public int hashCode() {

		if (!this.hashCodeComputed) {
			this.hashCode = Arrays.hashCode(this.bytes);
			this.hashCodeComputed = true;
		}
		return this.hashCode;
	}

	public EUID getID() {
		if (id == null) {
			id = new EUID(bytes, 0);
		}

		return id;
	}
}
