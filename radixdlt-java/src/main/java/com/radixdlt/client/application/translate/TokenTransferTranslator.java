package com.radixdlt.client.application.translate;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.radixdlt.client.application.actions.TokenTransfer;
import com.radixdlt.client.application.objects.Data;
import com.radixdlt.client.assets.Asset;
import com.radixdlt.client.core.RadixUniverse;
import com.radixdlt.client.core.address.RadixAddress;
import com.radixdlt.client.core.atoms.AccountReference;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.client.core.atoms.AtomBuilder;
import com.radixdlt.client.core.atoms.Consumable;
import com.radixdlt.client.core.atoms.Consumer;
import com.radixdlt.client.core.atoms.DataParticle;
import com.radixdlt.client.core.atoms.DataParticle.DataParticleBuilder;
import com.radixdlt.client.core.atoms.Payload;
import com.radixdlt.client.core.crypto.ECKeyPair;
import com.radixdlt.client.core.crypto.ECPublicKey;
import com.radixdlt.client.core.crypto.EncryptedPrivateKey;
import com.radixdlt.client.core.crypto.Encryptor;
import com.radixdlt.client.core.serialization.RadixJson;
import com.radixdlt.client.core.ledger.ParticleStore;
import io.reactivex.Completable;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TokenTransferTranslator {
	private final RadixUniverse universe;
	private static final JsonParser JSON_PARSER = new JsonParser();
	private final ParticleStore particleStore;

	public TokenTransferTranslator(RadixUniverse universe, ParticleStore particleStore) {
		this.universe = universe;
		this.particleStore = particleStore;
	}

	public TokenTransfer fromAtom(Atom atom) {
		List<SimpleImmutableEntry<ECPublicKey, Long>> summary =
			atom.summary().entrySet().stream()
				.filter(entry -> entry.getValue().containsKey(Asset.TEST.getId()))
				.map(entry -> new SimpleImmutableEntry<>(entry.getKey().iterator().next(), entry.getValue().get(Asset.TEST.getId())))
				.collect(Collectors.toList());

		if (summary.isEmpty()) {
			throw new IllegalStateException("Invalid atom: " + RadixJson.getGson().toJson(atom));
		}

		if (summary.size() > 2) {
			throw new IllegalStateException("More than two participants in token transfer. Unable to handle: " + summary);
		}

		final RadixAddress from;
		final RadixAddress to;
		if (summary.size() == 1) {
			from = summary.get(0).getValue() <= 0L ? universe.getAddressFrom(summary.get(0).getKey()) : null;
			to = summary.get(0).getValue() <= 0L ? null : universe.getAddressFrom(summary.get(0).getKey());
		} else {
			if (summary.get(0).getValue() > 0) {
				from = universe.getAddressFrom(summary.get(1).getKey());
				to = universe.getAddressFrom(summary.get(0).getKey());
			} else {
				from = universe.getAddressFrom(summary.get(0).getKey());
				to = universe.getAddressFrom(summary.get(1).getKey());
			}
		}

		final Optional<DataParticle> bytesParticle = atom.getDataParticles().stream()
			.filter(p -> !"encryptor".equals(p.getMetaData("application")))
			.findFirst();

		// Construct attachment from atom
		final Data attachment;
		if (bytesParticle.isPresent()) {
			Map<String, Object> metaData = new HashMap<>();

			final Optional<DataParticle> encryptorParticle = atom.getDataParticles().stream()
				.filter(p -> "encryptor".equals(p.getMetaData("application")))
				.findAny();
			metaData.put("encrypted", encryptorParticle.isPresent());

			final Encryptor encryptor;
			if (encryptorParticle.isPresent()) {
				JsonArray protectorsJson = JSON_PARSER.parse(encryptorParticle.get().getBytes().toUtf8String()).getAsJsonArray();
				List<EncryptedPrivateKey> protectors = new ArrayList<>();
				protectorsJson.forEach(protectorJson -> protectors.add(EncryptedPrivateKey.fromBase64(protectorJson.getAsString())));
				encryptor = new Encryptor(protectors);
			} else {
				encryptor = null;
			}
			attachment = Data.raw(bytesParticle.get().getBytes().getBytes(), metaData, encryptor);
		} else {
			attachment = null;
		}

		return TokenTransfer.create(from, to, Asset.TEST, Math.abs(summary.get(0).getValue()), attachment, atom.getTimestamp());
	}

	public Completable translate(TokenTransfer tokenTransfer, AtomBuilder atomBuilder) {
		return this.particleStore.getConsumables(tokenTransfer.getFrom())
			.firstOrError()
			.flatMapCompletable(unconsumedConsumables -> {

				// Translate attachment to corresponding atom structure
				final Data attachment = tokenTransfer.getAttachment();
				if (attachment != null) {
					atomBuilder.addDataParticle(new DataParticleBuilder().payload(new Payload(attachment.getBytes())).build());
					Encryptor encryptor = attachment.getEncryptor();
					if (encryptor != null) {
						JsonArray protectorsJson = new JsonArray();
						encryptor.getProtectors().stream().map(EncryptedPrivateKey::base64).forEach(protectorsJson::add);

						Payload encryptorPayload = new Payload(protectorsJson.toString().getBytes(StandardCharsets.UTF_8));
						DataParticle encryptorParticle = new DataParticleBuilder()
							.payload(encryptorPayload)
							.setMetaData("application", "encryptor")
							.setMetaData("contentType", "application/json")
							.build();
						atomBuilder.addDataParticle(encryptorParticle);
					}
				}

				long consumerTotal = 0;
				Iterator<Consumable> iterator = unconsumedConsumables.iterator();
				Map<Set<ECKeyPair>, Long> consumerQuantities = new HashMap<>();

				// HACK for now
				// TODO: remove this, create a ConsumersCreator
				// TODO: randomize this to decrease probability of collision
				while (consumerTotal < tokenTransfer.getSubUnitAmount() && iterator.hasNext()) {
					final long left = tokenTransfer.getSubUnitAmount() - consumerTotal;

					Consumer newConsumer = iterator.next().toConsumer();
					consumerTotal += newConsumer.getAmount();

					final long amount = Math.min(left, newConsumer.getAmount());
					newConsumer.addConsumerQuantities(amount, Collections.singleton(tokenTransfer.getTo().toECKeyPair()),
						consumerQuantities);

					atomBuilder.addConsumer(newConsumer);
				}

				if (consumerTotal < tokenTransfer.getSubUnitAmount()) {
					return Completable.error(new InsufficientFundsException(
						tokenTransfer.getTokenClass(), consumerTotal, tokenTransfer.getSubUnitAmount()
					));
				}

				List<Consumable> consumables = consumerQuantities.entrySet().stream()
					.map(entry -> new Consumable(entry.getValue(),
						entry.getKey().stream().map(ECKeyPair::getPublicKey).map(AccountReference::new)
							.collect(Collectors.toList()),
						System.nanoTime(), Asset.TEST.getId(), System.currentTimeMillis() * 60000L))
					.collect(Collectors.toList());
				atomBuilder.addConsumables(consumables);

				return Completable.complete();

				/*
				if (withPOWFee) {
					// TODO: Replace this with public key of processing node runner
					return atomBuilder.buildWithPOWFee(ledger.getMagic(), fromAddress.getPublicKey());
				} else {
					return atomBuilder.build();
				}
				*/
			});
	}
}
