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

package com.radixdlt.client.atommodel.validators;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;
import com.radixdlt.client.atommodel.Accountable;
import com.radixdlt.client.atommodel.Ownable;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.identifiers.RadixAddress;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.SerializerId2;

import java.util.Objects;
import java.util.Set;

@SerializerId2("radix.particles.registered_validator")
public class RegisteredValidatorParticle extends Particle implements Accountable, Ownable {
	@JsonProperty("address")
	@DsonOutput(DsonOutput.Output.ALL)
	private RadixAddress address;

	@JsonProperty("url")
	@DsonOutput(DsonOutput.Output.ALL)
	private String url;

	@JsonProperty("nonce")
	@DsonOutput(DsonOutput.Output.ALL)
	private long nonce;

	private RegisteredValidatorParticle() {
		// for serializer
	}

	public RegisteredValidatorParticle(RadixAddress address, long nonce) {
		this(address, null, nonce);
	}

	public RegisteredValidatorParticle(RadixAddress address, String url, long nonce) {
		super(address.euid());
		this.address = Objects.requireNonNull(address, "address");
		this.url = url;
		this.nonce = nonce;
	}

	@Override
	public RadixAddress getAddress() {
		return address;
	}

	public String getUrl() {
		return url;
	}

	public long getNonce() {
		return nonce;
	}

	@Override
	public String toString() {
		return String.format("%s[%s]", getClass().getSimpleName(), getAddress());
	}

	@Override
	public Set<RadixAddress> getAddresses() {
		return ImmutableSet.of(address);
	}
}
