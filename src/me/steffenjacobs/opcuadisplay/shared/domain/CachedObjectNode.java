package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedObjectNode extends CachedBaseNode {

	private UByte eventNotifier;

	public CachedObjectNode(UaObjectNode node) throws InterruptedException, ExecutionException {
		super(node);
		this.eventNotifier = FutureResolver.resolveFutureSafe(node.getEventNotifier());
	}

	protected CachedObjectNode(NodeId nodeId) {
		super(nodeId, NodeClass.Object);
	}

	public UByte getEventNotifier() {
		return eventNotifier;
	}

	public void setEventNotifier(UByte eventNotifier) {
		this.eventNotifier = eventNotifier;
	}

	public static CachedObjectNode create(int namespaceIndex, String name, int nodeId, CachedObjectTypeNode type) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedObjectNode cbn = new CachedObjectNode(id);
		cbn.setDisplayName(new LocalizedText("en", name));
		cbn.setBrowseName(new QualifiedName(namespaceIndex, name));
		List<CachedReference> refs = new ArrayList<>();
		CachedReference ref = new CachedReference("HasTypeDefinition", type.getBrowseName(), null, type.getNodeId());
		refs.add(ref);
		refs.addAll(type.getReferences());
		cbn.setReferences(refs);
		return cbn;
	}
}
