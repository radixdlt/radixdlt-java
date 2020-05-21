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


import com.radixdlt.identifiers.EUID;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.utils.Bytes;

/**
 * An application layer object representing some data found on the ledger.
 */
public class CRUDataUpdate {

	private final RRI rri;
	private final byte[] data;
	private final long timestamp;
	private final EUID actionId;

	public CRUDataUpdate(RRI rri, byte[] data, long timestamp, EUID actionId) {
        this.rri = rri;
        this.data = data;
        this.timestamp = timestamp;
        this.actionId = actionId;
    }

    /**
	 * The unique id for the this message action
	 * @return euid for the action
	 */
	public EUID getActionId() {
		return actionId;
	}

	public byte[] getData() {
		return data;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public RRI rri() {
        return rri;
    }

	@Override
	public String toString() {
		return timestamp + " " + rri + " " + Bytes.toHexString(data);
	}
}
