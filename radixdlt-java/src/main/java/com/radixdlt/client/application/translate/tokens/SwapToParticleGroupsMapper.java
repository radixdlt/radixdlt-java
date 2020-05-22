package com.radixdlt.client.application.translate.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.radixdlt.client.application.translate.ShardedParticleStateId;
import com.radixdlt.client.application.translate.StageActionException;
import com.radixdlt.client.application.translate.StatefulActionToParticleGroupsMapper;
import com.radixdlt.client.atommodel.tokens.AmmParticle;
import com.radixdlt.client.atommodel.tokens.MutableSupplyTokenDefinitionParticle.TokenTransition;
import com.radixdlt.client.atommodel.tokens.TokenPermission;
import com.radixdlt.client.atommodel.tokens.TransferrableTokensParticle;
import com.radixdlt.client.core.atoms.ParticleGroup;
import com.radixdlt.client.core.atoms.ParticleGroup.ParticleGroupBuilder;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.client.core.atoms.particles.SpunParticle;
import com.radixdlt.client.core.fungible.FungibleParticleTransitioner;
import com.radixdlt.client.core.fungible.FungibleParticleTransitioner.FungibleParticleTransition;
import com.radixdlt.client.core.fungible.NotEnoughFungiblesException;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.identifiers.RadixAddress;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.DsonOutput.Output;
import com.radixdlt.utils.UInt256;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SwapToParticleGroupsMapper implements StatefulActionToParticleGroupsMapper<SwapAction> {

	@Override
	public Set<ShardedParticleStateId> requiredState(SwapAction action) {
		return ImmutableSet.of(
			ShardedParticleStateId.of(AmmParticle.class, action.getAmmRri().getAddress()),
			ShardedParticleStateId.of(TransferrableTokensParticle.class, action.getAmmRri().getAddress())
		);
	}


	private FungibleParticleTransition<TransferrableTokensParticle, TransferrableTokensParticle> mapToParticles(SwapAction swapAction, List<TransferrableTokensParticle> currentParticles)
		throws NotEnoughFungiblesException {
		final UnaryOperator<List<TransferrableTokensParticle>> combiner =
			transferredList -> transferredList.stream()
				.map(TransferrableTokensParticle::getAmount)
				.reduce(UInt256::add)
				.map(amt -> Collections.singletonList(
					new TransferrableTokensParticle(
						amt,
						transferredList.get(0).getGranularity(),
						transferredList.get(0).getAddress(),
						System.nanoTime(),
						transferredList.get(0).getTokenDefinitionReference(),
						System.currentTimeMillis() / 60000L + 60000L,
						transferredList.get(0).getTokenPermissions()
					)
				)).orElse(Collections.emptyList());

		final FungibleParticleTransitioner<TransferrableTokensParticle, TransferrableTokensParticle> transitioner =
			new FungibleParticleTransitioner<>(
				(amt, consumable) -> new TransferrableTokensParticle(
					amt,
					consumable.getGranularity(),
					swapAction.getMyAddress(),
					System.nanoTime(),
					consumable.getTokenDefinitionReference(),
					System.currentTimeMillis() / 60000L + 60000L,
					consumable.getTokenPermissions()
				),
				combiner,
				(amt, consumable) -> new TransferrableTokensParticle(
					amt,
					consumable.getGranularity(),
					consumable.getAddress(),
					System.nanoTime(),
					consumable.getTokenDefinitionReference(),
					System.currentTimeMillis() / 60000L + 60000L,
					consumable.getTokenPermissions()
				),
				combiner,
				TransferrableTokensParticle::getAmount
			);

		return transitioner.createTransition(
			currentParticles,
			TokenUnitConversions.unitsToSubunits(swapAction.getAmount())
		);

	}

	@Override
	public List<ParticleGroup> mapToParticleGroups(SwapAction action, Stream<Particle> store) throws StageActionException {
		List<Particle> particles = store.collect(Collectors.toList());
		AmmParticle ammParticle = particles.stream()
			.filter(AmmParticle.class::isInstance)
			.map(AmmParticle.class::cast)
			.filter(p -> p.getRRI().equals(action.getAmmRri()))
			.findFirst()
			.orElseThrow(() -> new RuntimeException("Could not find AMM"));

		UInt256 invariant = ammParticle.getbAmount().multiply(ammParticle.getaAmount());
		UInt256 amountToSend = TokenUnitConversions.unitsToSubunits(action.getAmount());


		final UInt256 newaAmount;
		final UInt256 newbAmount;
		final UInt256 receiveAmount;
		final RRI receiveRRI;
		final RRI sendRRI;
		if (action.getTokenToSend().equals(ammParticle.getTokenA())) {
			newaAmount = ammParticle.getaAmount().add(amountToSend);
			newbAmount = invariant.divide(newaAmount);
			receiveAmount = ammParticle.getbAmount().subtract(newbAmount);
			sendRRI = ammParticle.getTokenA();
			receiveRRI = ammParticle.getTokenB();
		} else if (action.getTokenToSend().equals(ammParticle.getTokenB())) {
			newbAmount = ammParticle.getbAmount().add(amountToSend);
			newaAmount = invariant.divide(newbAmount);
			receiveAmount = ammParticle.getaAmount().subtract(newaAmount);
			sendRRI = ammParticle.getTokenB();
			receiveRRI = ammParticle.getTokenA();
		} else {
			throw new RuntimeException("Bad Tokens!");
		}

		AmmParticle nextAmmParticle = new AmmParticle(
			ammParticle.getRRI(),
			ammParticle.getTokenA(),
			ammParticle.getTokenB(),
			newaAmount,
			newbAmount
		);



		List<TransferrableTokensParticle> tokenConsumables = particles.stream()
			.filter(TransferrableTokensParticle.class::isInstance)
			.map(TransferrableTokensParticle.class::cast)
			.filter(p -> p.getTokenDefinitionReference().equals(sendRRI))
			.collect(Collectors.toList());

		ParticleGroupBuilder builder = ParticleGroup.builder();
		builder.addParticle(SpunParticle.down(ammParticle));
		builder.addParticle(SpunParticle.up(nextAmmParticle));
		List<SpunParticle> spunParticles = new ArrayList<>();

		try {
			FungibleParticleTransition<TransferrableTokensParticle, TransferrableTokensParticle> transition = this
				.mapToParticles(action, tokenConsumables);

			transition.getRemoved().stream().map(t -> (Particle) t).forEach(p -> spunParticles.add(SpunParticle.down(p)));
			transition.getMigrated().stream().map(t -> (Particle) t).forEach(p -> spunParticles.add(SpunParticle.up(p)));
			transition.getTransitioned().stream().map(t -> (Particle) t).forEach(p -> spunParticles.add(SpunParticle.up(p)));
			transition.getTransitioned().stream().map(t -> (Particle) t).forEach(p -> builder.addParticle(SpunParticle.down(p)));

		} catch (NotEnoughFungiblesException e) {
			throw new InsufficientFundsException(
				action.getTokenToSend(), TokenUnitConversions.subunitsToUnits(e.getCurrent()), action.getAmount()
			);
		}

		builder.addParticle(SpunParticle.up(new TransferrableTokensParticle(
			receiveAmount,
			UInt256.ONE,
			action.getMyAddress(),
			System.currentTimeMillis(),
			receiveRRI,
			System.currentTimeMillis(), ImmutableMap.of()
		)));

		return Arrays.asList(
			ParticleGroup.of(spunParticles),
			builder.build()
		);
	}
}
