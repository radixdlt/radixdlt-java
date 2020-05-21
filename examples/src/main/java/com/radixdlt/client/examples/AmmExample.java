package com.radixdlt.client.examples;

import com.radixdlt.client.application.RadixApplicationAPI;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.application.translate.tokens.AmmState;
import com.radixdlt.client.application.translate.tokens.CreateAmmAction;
import com.radixdlt.client.core.Bootstrap;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.utils.UInt256;

public class AmmExample {
	public static void main(String[] args) {
		// Create a new public key identity
		final RadixIdentity radixIdentity = RadixIdentities.createNew();

		// Initialize api layer
		RadixApplicationAPI api = RadixApplicationAPI.create(Bootstrap.LOCALHOST, radixIdentity);

		// Constantly sync account with network
		api.pull();

		System.out.println("My address: " + api.getAddress());
		System.out.println("My public key: " + api.getPublicKey());

		// Create a unique identifier for the token
		RRI tokenRRI = RRI.of(api.getAddress(), "JOSH");

		// Observe all past and future transactions
		api.observeTokenTransfers()
			.subscribe(System.out::println);

		// Observe current and future total balance
		api.observeBalance(tokenRRI)
			.subscribe(balance -> System.out.println("My Balance: " + balance));

		CreateAmmAction createAmmAction = new CreateAmmAction(
			RRI.of(api.getAddress(), "AMM"),
			RRI.of(api.getAddress(), "JOSH"),
			RRI.of(api.getAddress(), "MATT"),
			UInt256.FIVE,
			UInt256.EIGHT
		);

		api.observeState(AmmState.class, api.getAddress())
			.subscribe(System.out::println);

		api.execute(createAmmAction)
			.toObservable()
			.subscribe(System.out::println);
	}

}
