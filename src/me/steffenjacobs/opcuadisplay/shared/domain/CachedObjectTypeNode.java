package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectTypeNode;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedObjectTypeNode extends CachedBaseNode implements HasOnlyAbstract {
	
	private boolean isAbstract;

	public CachedObjectTypeNode(UaObjectTypeNode node) throws InterruptedException, ExecutionException {
		super(node);
		
		isAbstract = FutureResolver.resolveFutureSafe(node.getIsAbstract());
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
}
