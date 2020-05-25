/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.radixdlt.client.application.translate.data;


import java.util.Arrays;
import java.util.Objects;

import com.radixdlt.identifiers.EUID;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.utils.Bytes;

/**
 * An application layer object representing some data found on the ledger.
 */
public class CRUDataUpdate {

	private final RRI rri;
	private final byte[] data;
	private final EUID actionId;
	private final transient long timestamp;

	public CRUDataUpdate(RRI rri, byte[] data, long timestamp, EUID actionId) {
        this.rri = rri;
        this.data = data;
        this.timestamp = timestamp;
        this.actionId = actionId;
    }

    /**
	 * The unique id for the this update action.
	 * @return {@link EUID} for the action
	 */
	public EUID getActionId() {
		return this.actionId;
	}

	/**
	 * The data from this update action.
	 * @return the data for the action
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * The timestamp from this update action.
	 * <p>
	 * Timestamp is in milliseconds since Unix epoch.
	 *
	 * @return the timestamp for the action
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * The resource identifier for this update action.
	 * @return the resource identifier for the action
	 */
	public RRI rri() {
        return rri;
    }

	@Override
	public int hashCode() {
		return Objects.hash(this.actionId, this.rri)
			* 31 + Arrays.hashCode(this.data);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof CRUDataUpdate) {
			CRUDataUpdate that = (CRUDataUpdate) obj;
			return Objects.equals(this.actionId, that.actionId)
				&& Objects.equals(this.rri, that.rri)
				&& Arrays.equals(this.data, that.data);
		}
		return false;
	}

	@Override
	public String toString() {
		return timestamp + " " + rri + " " + Bytes.toHexString(data);
	}
}
