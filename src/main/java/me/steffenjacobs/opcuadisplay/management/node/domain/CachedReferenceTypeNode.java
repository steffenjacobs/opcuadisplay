package me.steffenjacobs.opcuadisplay.management.node.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaReferenceTypeNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.opcInterface.opcClient.FutureResolver;
/** @author Steffen Jacobs */
public class CachedReferenceTypeNode extends CachedBaseNode {

	private boolean isAbstract;
	private boolean symmetric;
	private LocalizedText inverseName;

	public CachedReferenceTypeNode(UaReferenceTypeNode node) throws InterruptedException, ExecutionException {
		super(node);

		isAbstract = FutureResolver.resolveFutureSafe(node.getIsAbstract());
		symmetric = FutureResolver.resolveFutureSafe(node.getSymmetric());
		inverseName = FutureResolver.resolveFutureSafe(node.getInverseName());
	}

	protected CachedReferenceTypeNode(CachedReferenceTypeNode node) {
		super(node);
		this.isAbstract = node.isAbstract;
		this.symmetric = node.symmetric;
		this.inverseName = new LocalizedText(node.inverseName.getLocale(), node.inverseName.getText());
	}

	public CachedReferenceTypeNode(NodeId nodeId) {
		super(nodeId, NodeClass.ReferenceType);
		this.inverseName = new LocalizedText("", "");
	}

	@Override
	public CachedReferenceTypeNode duplicate() {
		return new CachedReferenceTypeNode(this);
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public boolean isSymmetric() {
		return symmetric;
	}

	public void setSymmetric(boolean symmetric) {
		this.symmetric = symmetric;
	}

	public LocalizedText getInverseName() {
		return inverseName;
	}

	public void setInverseName(LocalizedText inverseName) {
		this.inverseName = inverseName;
	}
}
