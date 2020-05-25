/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */
package com.radixdlt.client.atommodel.cru;

import javax.annotation.concurrent.Immutable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.atommodel.Identifiable;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.DsonOutput.Output;
import com.radixdlt.serialization.SerializerId2;
import com.radixdlt.utils.Bytes;

/**
 * Particle representing data that can be created, read and updated.
 */
@Immutable
@SerializerId2("radix.particles.cru")
public final class CRUDataParticle extends Particle implements Identifiable {

	// Account and lookup key
	@JsonProperty("rri")
	@DsonOutput(Output.ALL)
	private RRI rri;

	// Serial / version number for updates
	// First creation must == 0, updates incrementing monotonically
	@JsonProperty("serialno")
	@DsonOutput(Output.ALL)
	private long serialno;

	// The actual data
	@JsonProperty("data")
	@DsonOutput(Output.ALL)
	private byte[] data;

	CRUDataParticle() {
		super();
	}

	public CRUDataParticle(RRI rri, long serialno, byte[] data) {
		super(rri.getAddress().euid());

		this.rri = rri;
		this.serialno = serialno;
		this.data = data;
	}

	@Override
	public RRI getRRI() {
		return this.rri;
	}

	/**
	 * Retrieve the serial number of this particle.
	 * @return the serial number.
	 */
	public long serialno() {
		return this.serialno;
	}

	/**
	 * Retrieve the data for this particle.
	 * <p>
	 * Note that callers should not modify the contents of the returned array.
	 * @return the particle data.
	 */
	public byte[] data() {
		return this.data;
	}

	@Override
	public String toString() {
		String stringData = this.data == null ? "" : Bytes.toHexString(this.data);
		return String.format("%s[(%s:%s), (%s)]", getClass().getSimpleName(), String.valueOf(this.rri), this.serialno, stringData);
	}
}
