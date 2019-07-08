package com.radixdlt.client.core.network.epics;

import com.radixdlt.client.core.network.RadixNetworkEpic;
import com.radixdlt.client.core.network.RadixNetworkState;
import com.radixdlt.client.core.network.RadixNode;
import com.radixdlt.client.core.network.RadixNodeAction;
import com.radixdlt.client.core.network.actions.ObserveAtomSubscribeAction;
import com.radixdlt.client.core.network.actions.ObserveAtomStatusAction;
import com.radixdlt.client.core.network.actions.ObserveAtomSubscribedAction;
import com.radixdlt.client.core.network.actions.ObserveAtomUnsubscribeAction;
import com.radixdlt.client.core.network.jsonrpc.RadixJsonRpcClient;
import com.radixdlt.client.core.network.jsonrpc.RadixJsonRpcClient.NotificationType;
import com.radixdlt.client.core.network.websocket.WebSocketClient;
import com.radixdlt.client.core.network.websocket.WebSocketStatus;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ObserveAtomEpic implements RadixNetworkEpic {
	private static final int DELAY_CLOSE_SECS = 5;

	private final WebSockets webSockets;
	private final int timeoutSecs = 30;

	public ObserveAtomEpic(WebSockets webSockets) {
		this.webSockets = webSockets;
	}

	private Completable waitForConnection(RadixNode node) {
		final WebSocketClient ws = webSockets.getOrCreate(node);
		return ws.getState()
			.doOnNext(s -> {
				if (s.equals(WebSocketStatus.DISCONNECTED)) {
					ws.connect();
				}
			})
			.filter(s -> s.equals(WebSocketStatus.CONNECTED))
			.firstOrError()
			.ignoreElement();
	}

	private Observable<RadixNodeAction> observeAtom(ObserveAtomSubscribeAction request) {
		final WebSocketClient ws = webSockets.getOrCreate(request.getNode());
		final RadixJsonRpcClient jsonRpcClient = new RadixJsonRpcClient(ws);
		final String subscriberId = UUID.randomUUID().toString();

		return jsonRpcClient.observeAtomStatusNotifications(subscriberId)
			.<RadixNodeAction>concatMapSingle(notification -> {
				if (notification.getType().equals(NotificationType.START)) {
					return jsonRpcClient.sendGetAtomStatusNotifications(subscriberId, request.getAID())
						.andThen(Single.just(ObserveAtomSubscribedAction.of(request.getUuid(), request.getAID(), request.getNode())));
				} else {
					return Single.just(ObserveAtomStatusAction.fromStatusEvent(
						request.getUuid(),
						request.getAID(),
						request.getNode(),
						notification.getEvent()
					));
				}
			})
			.doOnDispose(() -> {
				jsonRpcClient.closeAtomStatusNotifications(subscriberId)
					.andThen(
						Observable.timer(DELAY_CLOSE_SECS, TimeUnit.SECONDS)
							.flatMapCompletable(t -> {
								ws.close();
								return Completable.complete();
							})
					).subscribe();
			})
			.doFinally(() -> Observable.timer(DELAY_CLOSE_SECS, TimeUnit.SECONDS).subscribe(t -> ws.close()));
	}

	@Override
	public Observable<RadixNodeAction> epic(Observable<RadixNodeAction> actions, Observable<RadixNetworkState> networkState) {
		return actions.ofType(ObserveAtomSubscribeAction.class)
				.flatMap(a -> {
					final RadixNode node = a.getNode();
					return waitForConnection(node)
						.andThen(this.observeAtom(a))
						.takeUntil(
							actions.ofType(ObserveAtomUnsubscribeAction.class)
								.filter(u -> u.getUuid().equals(a.getUuid()))
						);
				});
	}
}
