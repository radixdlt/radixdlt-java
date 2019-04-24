package org.radix.crypto;

// Note that all methods must be thread safe
interface Hasher {
	byte[] hash256Raw(byte[] data);

	byte[] hash256(byte[] data);
	byte[] hash256(byte[] data, int offset, int length);

	byte[] hash256(byte[] data0, byte[] data1);

	byte[] hash512(byte[] data, int offset, int length);
}
