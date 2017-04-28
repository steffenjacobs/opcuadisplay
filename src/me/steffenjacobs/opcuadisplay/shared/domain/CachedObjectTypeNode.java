package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedObjectTypeNode extends CachedBaseNode implements HasOnlyAbstract {
	
	private boolean isAbstract;

	public CachedObjectTypeNode(UaObjectTypeNode node) throws InterruptedException, ExecutionException {
		super(node);
		
		isAbstract = FutureResolver.resolveFutureSafe(node.getIsAbstract());
	}
	
	protected CachedObjectTypeNode(CachedObjectTypeNode node, NodeId nodeId) {
		super(node, nodeId);
		this.isAbstract = node.isAbstract;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
}
