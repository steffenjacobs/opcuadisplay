package me.steffenjacobs.opcuadisplay.management.node.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;
import me.steffenjacobs.opcuadisplay.opcInterface.opcClient.FutureResolver;
/** @author Steffen Jacobs */
public class CachedObjectTypeNode extends CachedBaseNode implements HasOnlyAbstract {
	
	private boolean isAbstract;

	public CachedObjectTypeNode(UaObjectTypeNode node) throws InterruptedException, ExecutionException {
		super(node);
		
		isAbstract = FutureResolver.resolveFutureSafe(node.getIsAbstract());
	}
	
	protected CachedObjectTypeNode(CachedObjectTypeNode node) {
		super(node);
		this.isAbstract = node.isAbstract;
	}
	
	public CachedObjectTypeNode(NodeId nodeId) {
		super(nodeId, NodeClass.ObjectType);
	}

	@Override
	public CachedObjectTypeNode duplicate() {
		return new CachedObjectTypeNode(this);
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public static CachedObjectTypeNode create(int namespaceIndex, String name, int nodeId) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedObjectTypeNode cbn = new CachedObjectTypeNode(id);
		cbn.setDisplayName(new LocalizedText("en", name));
		cbn.setBrowseName(new QualifiedName(namespaceIndex, name));	
		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(cbn);
		
		//set abstract to false per default
		cbn.setAbstract(false);
		return cbn;
	}
}
