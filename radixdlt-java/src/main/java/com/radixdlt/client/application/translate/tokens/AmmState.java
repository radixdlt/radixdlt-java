package com.radixdlt.client.application.translate.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.application.translate.ApplicationState;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.utils.UInt256;

public class AmmState implements ApplicationState {
	private final RRI rri;
	private final RRI tokenA;
	private final RRI tokenB;
	private final UInt256 aAmount;
	private final UInt256 bAmount;

	public AmmState(RRI rri, RRI tokenA, RRI tokenB, UInt256 aAmount, UInt256 bAmount) {
		this.rri = rri;
		this.tokenA = tokenA;
		this.tokenB = tokenB;
		this.aAmount = aAmount;
		this.bAmount = bAmount;
	}

	public String toString() {
		return rri + " {a: " + tokenA + " b: " + tokenB + " aAmount: " + aAmount + " bAmount: " + bAmount + "}";
	}
}
