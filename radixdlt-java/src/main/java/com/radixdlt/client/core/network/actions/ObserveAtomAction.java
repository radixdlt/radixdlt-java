package com.radixdlt.client.core.network.actions;

import com.radixdlt.client.core.network.RadixNodeAction;
import org.radix.common.ID.AID;

public interface ObserveAtomAction extends RadixNodeAction {

	String getUuid();

	AID getAID();
}
