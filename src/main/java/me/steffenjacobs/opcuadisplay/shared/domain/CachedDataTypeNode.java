package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaDataTypeNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;

public class CachedDataTypeNode extends CachedBaseNode implements HasOnlyAbstract {

	private boolean isAbstract;

	public CachedDataTypeNode(UaDataTypeNode node) throws InterruptedException, ExecutionException {
		super(node);

		isAbstract = FutureResolver.resolveFutureSafe(node.getIsAbstract());
	}

	protected CachedDataTypeNode(CachedDataTypeNode node) {
		super(node);
		this.isAbstract = node.isAbstract;
	}

	public CachedDataTypeNode(NodeId nodeId) {
		super(nodeId, NodeClass.DataType);
	}

	@Override
	public CachedDataTypeNode duplicate() {
		return new CachedDataTypeNode(this);
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public static CachedDataTypeNode create(int namespaceIndex, String name, int nodeId) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedDataTypeNode cbn = new CachedDataTypeNode(id);
		cbn.setDisplayName(new LocalizedText("en", name));
		cbn.setBrowseName(new QualifiedName(namespaceIndex, name));
		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(cbn);

		// set abstract to true per default
		cbn.setAbstract(true);
		return cbn;
	}
}
