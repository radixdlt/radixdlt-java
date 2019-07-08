package com.radixdlt.client.core.network.actions;

import com.radixdlt.client.core.network.RadixNode;
import com.radixdlt.client.core.network.RadixNodeAction;
import java.util.Objects;
import org.radix.common.ID.AID;

public class ObserveAtomSubscribedAction implements ObserveAtomAction {
	private final String uuid;
	private final AID aid;
	private final RadixNode node;

	private ObserveAtomSubscribedAction(String uuid, AID aid, RadixNode node) {
		this.uuid = Objects.requireNonNull(uuid);
		this.aid = Objects.requireNonNull(aid);
		this.node = Objects.requireNonNull(node);
	}

	public static ObserveAtomSubscribedAction of(String uuid, AID aid, RadixNode node) {
		return new ObserveAtomSubscribedAction(uuid, aid, node);
	}

	@Override
	public String getUuid() {
		return this.uuid;
	}

	@Override
	public AID getAID() {
		return this.aid;
	}

	@Override
	public RadixNode getNode() {
		return this.node;
	}

	@Override
	public String toString() {
		return "OBSERVE_ATOM_SUBSCRIBED " + this.uuid + " " + aid + " " + this.node;
	}
}
