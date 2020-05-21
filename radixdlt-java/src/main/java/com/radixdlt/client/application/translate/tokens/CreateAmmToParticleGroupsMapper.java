package com.radixdlt.client.application.translate.tokens;

import com.radixdlt.client.application.translate.ShardedParticleStateId;
import com.radixdlt.client.application.translate.StageActionException;
import com.radixdlt.client.application.translate.StatefulActionToParticleGroupsMapper;
import com.radixdlt.client.atommodel.rri.RRIParticle;
import com.radixdlt.client.atommodel.tokens.AmmParticle;
import com.radixdlt.client.atommodel.tokens.TransferrableTokensParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CreateAmmToParticleGroupsMapper implements StatefulActionToParticleGroupsMapper<CreateAmmAction> {
	@Override
	public Set<ShardedParticleStateId> requiredState(CreateAmmAction action) {
		return Collections.singleton(
			ShardedParticleStateId.of(TransferrableTokensParticle.class, action.getRRI().getAddress())
		);
	}

	@Override
	public List<ParticleGroup> mapToParticleGroups(CreateAmmAction action, Stream<Particle> store) throws StageActionException {
		AmmParticle ammParticle = new AmmParticle(
			action.getRRI(),
			action.getTokenA(),
			action.getTokenB(),
			action.getaAmount(),
			action.getbAmount()
		);
		RRIParticle rriParticle = new RRIParticle(action.getRRI());

		List<Particle> particles = store.collect(Collectors.toList());

		TransferrableTokensParticle tokensA = particles.stream()
			.filter(TransferrableTokensParticle.class::isInstance)
			.map(TransferrableTokensParticle.class::cast)
			.filter(p -> p.getTokenDefinitionReference().equals(action.getTokenA()))
			.findFirst().orElseThrow(() -> new RuntimeException("No tokens!"));

		TransferrableTokensParticle tokensB = particles.stream()
			.filter(TransferrableTokensParticle.class::isInstance)
			.map(TransferrableTokensParticle.class::cast)
			.filter(p -> p.getTokenDefinitionReference().equals(action.getTokenB()))

			.findFirst().orElseThrow(() -> new RuntimeException("No tokens!"));
		return Collections.singletonList(ParticleGroup.of(
			SpunParticle.down(rriParticle),
			SpunParticle.up(ammParticle),
			SpunParticle.down(tokensA),
			SpunParticle.down(tokensB)
		));
	}
}
