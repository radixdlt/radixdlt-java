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

import java.util.Objects;

import com.radixdlt.client.application.translate.Action;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.utils.Bytes;

/**
 * An Action object which updates a CRU data object.
 */
public final class UpdateCRUDataAction implements Action {
    private final RRI rri;
    private final byte[] data;

    private UpdateCRUDataAction(RRI rri, byte[] data) {
        this.rri = rri;
        this.data = Objects.requireNonNull(data);
    }

    public static UpdateCRUDataAction create(RRI rri, byte[] data) {
        return new UpdateCRUDataAction(rri, data);
    }

    public byte[] getData() {
        return this.data;
    }

    public RRI getRRI() {
        return this.rri;
    }

    @Override
    public String toString() {
    	String dataString = this.data == null ? "null" : Bytes.toBase64String(this.data);
        return String.format("%s[%s:%s]", getClass().getSimpleName(), this.rri, dataString);
    }
}
