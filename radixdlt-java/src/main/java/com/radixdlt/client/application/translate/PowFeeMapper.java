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

package com.radixdlt.client.application.translate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.crypto.Hash;
import com.radixdlt.client.core.pow.ProofOfWork;
import com.radixdlt.client.core.pow.ProofOfWorkBuilder;
import com.radixdlt.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Maps a complete list of particles ready to be submitted to a POW fee particle.
 */
public class PowFeeMapper implements FeeMapper {
	private static final int LEADING = 16;

	private final Function<Atom, Hash> hasher;
	private final int universeMagic;
	private final ProofOfWorkBuilder powBuilder;

	public PowFeeMapper(Function<Atom, Hash> hasher, int universeMagic, ProofOfWorkBuilder powBuilder) {
		this.hasher = Objects.requireNonNull(hasher, "hasher is required");
		this.universeMagic = universeMagic;
		this.powBuilder = Objects.requireNonNull(powBuilder, "powBuilder is required");
	}

	@Override
	public Pair<Map<String, String>, List<ParticleGroup>> map(RadixApplicationAPI api, Atom atom) {
		final byte[] seed = this.hasher.apply(atom).toByteArray();
		ProofOfWork pow = this.powBuilder.build(universeMagic, seed, LEADING);

		return Pair.of(ImmutableMap.of(Atom.METADATA_POW_NONCE_KEY, String.valueOf(pow.getNonce())), ImmutableList.of());
	}
}
