package com.radixdlt.client.application.translate.tokens;

import com.radixdlt.client.application.translate.Action;
import com.radixdlt.identifiers.RRI;
import com.radixdlt.identifiers.RadixAddress;
import java.math.BigDecimal;

public class SwapAction implements Action {
	private final RadixAddress myAddress;
	private final RRI ammRri;
	private final RRI tokenToSend;
	private final BigDecimal amount;

	public SwapAction(RadixAddress myAddress, RRI ammRri, RRI tokenToSend, BigDecimal amount) {
		this.myAddress = myAddress;
		this.ammRri = ammRri;
		this.tokenToSend = tokenToSend;
		this.amount = amount;
	}

	public RadixAddress getMyAddress() {
		return myAddress;
	}

	public RRI getAmmRri() {
		return ammRri;
	}

	public RRI getTokenToSend() {
		return tokenToSend;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
