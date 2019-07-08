package com.radixdlt.client.core.network.actions;

import com.radixdlt.client.core.network.RadixNode;
import com.radixdlt.client.core.network.RadixNodeAction;
import java.util.Objects;
import org.radix.common.ID.AID;

public class ObserveAtomSubscribeAction implements ObserveAtomAction {
	private final String uuid;
	private final RadixNode node;
	private final AID aid;

	private ObserveAtomSubscribeAction(String uuid, AID aid, RadixNode node) {
		Objects.requireNonNull(uuid);
		Objects.requireNonNull(aid);
		Objects.requireNonNull(node);

		this.uuid = uuid;
		this.aid = aid;
		this.node = node;
	}

	public static ObserveAtomSubscribeAction of(String uuid, AID aid, RadixNode node) {
		return new ObserveAtomSubscribeAction(uuid, aid, node);
	}

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public AID getAID() {
		return aid;
	}

	@Override
	public RadixNode getNode() {
		return node;
	}

	@Override
	public String toString() {
		return "OBSERVE_ATOM_SEND " + uuid + " " + aid + " " + node;
	}
}
