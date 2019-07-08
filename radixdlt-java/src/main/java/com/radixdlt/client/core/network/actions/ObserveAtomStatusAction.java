package com.radixdlt.client.core.network.actions;

import com.radixdlt.client.core.atoms.AtomStatusEvent;
import com.radixdlt.client.core.network.RadixNode;
import java.util.Objects;
import org.radix.common.ID.AID;

public class ObserveAtomStatusAction implements ObserveAtomAction {
	private final String uuid;
	private final AID aid;
	private final RadixNode node;
	private final AtomStatusEvent statusEvent;

	private ObserveAtomStatusAction(String uuid, AID aid, RadixNode node, AtomStatusEvent statusEvent) {
		this.uuid = Objects.requireNonNull(uuid);
		this.aid = Objects.requireNonNull(aid);
		this.node = Objects.requireNonNull(node);
		this.statusEvent = Objects.requireNonNull(statusEvent);
	}

	public static ObserveAtomStatusAction fromStatusEvent(String uuid, AID aid, RadixNode node, AtomStatusEvent statusEvent) {
		return new ObserveAtomStatusAction(uuid, aid, node, statusEvent);
	}

	/**
	 * The end result type of the atom submission
	 *
	 * @return The end result type
	 */
	public AtomStatusEvent statusEvent() {
		return this.statusEvent;
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
		return "OBSERVE_ATOM_STATUS " + this.uuid + " " + this.aid + " " + this.node + " " + this.statusEvent;
	}
}
