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

package com.radixdlt.client.core.network.actions;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.radixdlt.identifiers.RadixAddress;
import com.radixdlt.client.core.ledger.AtomObservation;
import com.radixdlt.client.core.network.RadixNode;
import org.junit.Test;

public class FetchAtomsObservationActionTest {
	@Test
	public void when_initializing_with_null__npe_should_be_thrown() {
		assertThatThrownBy(() -> FetchAtomsObservationAction.of(
			null,
			mock(RadixAddress.class),
			mock(RadixNode.class),
			mock(AtomObservation.class)
		)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> FetchAtomsObservationAction.of(
			"Hi",
			null,
			mock(RadixNode.class),
			mock(AtomObservation.class)
		)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> FetchAtomsObservationAction.of(
			"Hi",
			mock(RadixAddress.class),
			null,
			mock(AtomObservation.class)
		)).isInstanceOf(NullPointerException.class);

		assertThatThrownBy(() -> FetchAtomsObservationAction.of(
			"Hi",
			mock(RadixAddress.class),
			mock(RadixNode.class),
			null
		)).isInstanceOf(NullPointerException.class);
	}
}