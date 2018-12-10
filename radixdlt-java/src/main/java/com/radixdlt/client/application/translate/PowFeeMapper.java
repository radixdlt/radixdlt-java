package com.radixdlt.client.application.translate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.radix.utils.Int128;
import org.radix.utils.UInt256;

import com.radixdlt.client.atommodel.tokens.FeeParticle;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.atoms.RadixHash;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.crypto.ECPublicKey;
import com.radixdlt.client.core.pow.ProofOfWork;
import com.radixdlt.client.core.pow.ProofOfWorkBuilder;

/**
 * Maps a complete list of particles ready to be submitted to a POW fee particle.
 */
public class PowFeeMapper implements FeeMapper {
	private static final int LEADING = 16;

	private final Function<List<SpunParticle>, RadixHash> hasher;
	private final ProofOfWorkBuilder powBuilder;

	public PowFeeMapper(Function<List<SpunParticle>, RadixHash> hasher, ProofOfWorkBuilder powBuilder) {
		this.hasher = hasher;
		this.powBuilder = powBuilder;
	}

	@Override
	public List<SpunParticle> map(List<SpunParticle> particles, RadixUniverse universe, ECPublicKey key) {
		Objects.requireNonNull(key);
		Objects.requireNonNull(universe);
		Objects.requireNonNull(particles);

		final byte[] seed = hasher.apply(particles).toByteArray();
		ProofOfWork pow = powBuilder.build(universe.getMagic(), seed, LEADING);

		Particle fee = new FeeParticle(
				fromNonce(pow.getNonce()),
				universe.getAddressFrom(key),
				System.nanoTime(),
				universe.getPOWToken(),
				System.currentTimeMillis() * 60000
		);

		return Collections.singletonList(SpunParticle.up(fee));
	}

	private static UInt256 fromNonce(long nonce) {
		return UInt256.from(Int128.from(0L, nonce));
	}
}