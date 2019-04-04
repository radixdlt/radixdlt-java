package com.radixdlt.client.application.translate.data;

import com.google.gson.annotations.Expose;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import org.bouncycastle.util.encoders.Base64;
import org.radix.utils.RadixConstants;

/**
 * An application layer object representing some data found on the ledger.
 */
public class DecryptedMessage {
	public enum EncryptionState {
		/**
		 * Specifies that the data in the DecryptedMessage object WAS originally
		 * encrypted and has been successfully decrypted to it's present byte array.
		 */
		DECRYPTED,
		/**
		 * Specifies that the data in the DecryptedMessage object was NOT
		 * encrypted and the present data byte array just represents the original data.
		 */
		NOT_ENCRYPTED,
		/**
		 * Specifies that the data in the DecryptedMessage object WAS encrypted
		 * but could not be decrypted. The present data byte array represents the still
		 * encrypted data.
		 */
		CANNOT_DECRYPT,
	}

	@Expose
	private final RadixAddress from;
	@Expose
	private final RadixAddress to;
	private final byte[] data;
	private final EncryptionState encryptionState;
	@Expose
	private final long timestamp;
	@Expose
	private final String message;

	public DecryptedMessage(byte[] data, RadixAddress from, RadixAddress to, EncryptionState encryptionState, long timestamp) {
		this.from = from;
		this.data = data;
		this.message = new String(data, RadixConstants.STANDARD_CHARSET);
		this.to = to;
		this.encryptionState = encryptionState;
		this.timestamp = timestamp;
	}

	public EncryptionState getEncryptionState() {
		return encryptionState;
	}

	public byte[] getData() {
		return data;
	}

	public RadixAddress getTo() {
		return to;
	}

	public RadixAddress getFrom() {
		return from;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return timestamp + " " + from + " -> " + to + ": " + encryptionState + " " + Base64.toBase64String(data);
	}
}
