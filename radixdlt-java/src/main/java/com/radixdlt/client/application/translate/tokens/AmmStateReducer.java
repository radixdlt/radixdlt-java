package com.radixdlt.client.application.translate.tokens;

import com.radixdlt.client.application.translate.ParticleReducer;
import com.radixdlt.client.atommodel.tokens.AmmParticle;
import com.radixdlt.client.core.atoms.particles.Particle;

public class AmmStateReducer implements ParticleReducer<AmmState> {

	@Override
	public Class<AmmState> stateClass() {
		return AmmState.class;
	}

	@Override
	public AmmState initialState() {
		return new AmmState(null, null, null, null, null);
	}

	@Override
	public AmmState reduce(AmmState state, Particle p) {
		if (p instanceof AmmParticle) {
			AmmParticle ammParticle = (AmmParticle) p;
			return new AmmState(
				ammParticle.getRRI(),
				ammParticle.getTokenA(),
				ammParticle.getTokenB(),
				ammParticle.getaAmount(),
				ammParticle.getbAmount()
			);
		}
		return state;
	}

	@Override
	public AmmState combine(AmmState state0, AmmState state1) {
		return state1;
	}
}
