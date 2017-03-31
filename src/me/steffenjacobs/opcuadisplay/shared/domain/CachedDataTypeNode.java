package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaDataTypeNode;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedDataTypeNode extends CachedBaseNode implements HasOnlyAbstract{

	private boolean isAbstract;

	public CachedDataTypeNode(UaDataTypeNode node) throws InterruptedException, ExecutionException {
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
