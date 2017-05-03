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

	protected CachedViewNode(CachedViewNode node) {
		super(node);
		this.containsNoLoop = node.containsNoLoop;
		this.eventNotifier = UByte.valueOf(node.eventNotifier.intValue());
	}

	@Override
	public CachedViewNode duplicate() {
		return new CachedViewNode(this);
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
