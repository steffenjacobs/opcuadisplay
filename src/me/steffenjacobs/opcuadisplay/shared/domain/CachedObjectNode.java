package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedObjectNode extends CachedBaseNode {

	private UByte eventNotifier;

	public CachedObjectNode(UaObjectNode node) throws InterruptedException, ExecutionException {
		super(node);

		this.eventNotifier = FutureResolver.resolveFutureSafe(node.getEventNotifier());
	}

	public UByte getEventNotifier() {
		return eventNotifier;
	}

	public void setEventNotifier(UByte eventNotifier) {
		this.eventNotifier = eventNotifier;
	}
}
