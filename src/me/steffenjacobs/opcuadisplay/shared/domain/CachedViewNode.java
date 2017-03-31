package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaViewNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedViewNode extends CachedBaseNode {

	private boolean containsNoLoop;
	private UByte eventNotifier;

	public CachedViewNode(UaViewNode node) throws InterruptedException, ExecutionException {
		super(node);

		containsNoLoop = FutureResolver.resolveFutureSafe(node.getContainsNoLoops());
		eventNotifier = FutureResolver.resolveFutureSafe(node.getEventNotifier());
	}

	public boolean isContainsNoLoop() {
		return containsNoLoop;
	}

	public void setContainsNoLoop(boolean containsNoLoop) {
		this.containsNoLoop = containsNoLoop;
	}

	public UByte getEventNotifier() {
		return eventNotifier;
	}

	public void setEventNotifier(UByte eventNotifier) {
		this.eventNotifier = eventNotifier;
	}
}
