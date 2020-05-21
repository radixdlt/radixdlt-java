package com.radixdlt.client.application.translate.tokens;

import com.radixdlt.client.application.translate.StatelessActionToParticleGroupsMapper;
import com.radixdlt.client.atommodel.rri.RRIParticle;
import com.radixdlt.client.atommodel.tokens.AmmParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import java.util.Collections;
import java.util.List;

public class CreateAmmToParticleGroupsMapper implements StatelessActionToParticleGroupsMapper<CreateAmmAction> {
	@Override
	public List<ParticleGroup> mapToParticleGroups(CreateAmmAction ammCreation) {
		AmmParticle ammParticle = new AmmParticle(
			ammCreation.getRRI(),
			ammCreation.getTokenA(),
			ammCreation.getTokenB(),
			ammCreation.getaAmount(),
			ammCreation.getbAmount()
		);
		RRIParticle rriParticle = new RRIParticle(ammCreation.getRRI());

		return Collections.singletonList(ParticleGroup.of(
			SpunParticle.down(rriParticle),
			SpunParticle.up(ammParticle)
		));
	}
}
