package com.radixdlt.client.atommodel.tokens;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.atommodel.Accountable;
import com.radixdlt.client.core.atoms.particles.Particle;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.identifiers.RadixAddress;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.SerializerId2;
import com.radixdlt.utils.UInt256;
import java.util.Collections;
import java.util.Set;

@SerializerId2("radix.particles.amm")
public class AmmParticle extends Particle implements Accountable {
	@JsonProperty("rri")
	@DsonOutput(DsonOutput.Output.ALL)
	private RRI rri;

	@JsonProperty("tokenA")
	@DsonOutput(DsonOutput.Output.ALL)
	private RRI tokenA;

	@JsonProperty("tokenB")
	@DsonOutput(DsonOutput.Output.ALL)
	private RRI tokenB;

	@JsonProperty("aAmount")
	@DsonOutput(DsonOutput.Output.ALL)
	private UInt256 aAmount;

	@JsonProperty("bAmount")
	@DsonOutput(DsonOutput.Output.ALL)
	private UInt256 bAmount;


	public AmmParticle(
		RRI rri,
		RRI tokenA,
		RRI tokenB,
		UInt256 aAmount,
		UInt256 bAmount
	) {
		super(rri.getAddress().euid());
		this.rri = rri;
		this.tokenA = tokenA;
		this.tokenB = tokenB;
		this.aAmount = aAmount;
		this.bAmount = bAmount;
	}

	public RRI getRRI() {
		return rri;
	}

	public RRI getTokenA() {
		return tokenA;
	}

	public RRI getTokenB() {
		return tokenB;
	}

	public UInt256 getaAmount() {
		return aAmount;
	}

	public UInt256 getbAmount() {
		return bAmount;
	}

	@Override
	public Set<RadixAddress> getAddresses() {
		return Collections.singleton(rri.getAddress());
	}
}
