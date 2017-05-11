package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;

public class CachedObjectNode extends CachedBaseNode {

	private UByte eventNotifier;

	public CachedObjectNode(UaObjectNode node) throws InterruptedException, ExecutionException {
		super(node);
		this.eventNotifier = FutureResolver.resolveFutureSafe(node.getEventNotifier());
	}

	public CachedObjectNode(NodeId nodeId) {
		super(nodeId, NodeClass.Object);
		this.eventNotifier = UByte.valueOf(0);
	}

	public UByte getEventNotifier() {
		return eventNotifier;
	}

	public void setEventNotifier(UByte eventNotifier) {
		this.eventNotifier = eventNotifier;
	}

	protected CachedObjectNode(CachedObjectNode node) {
		super(node);
		this.setEventNotifier(
				node.eventNotifier != null ? UByte.valueOf(node.eventNotifier.intValue()) : UByte.valueOf(0));
	}

	@Override
	public CachedObjectNode duplicate() {
		return new CachedObjectNode(this);
	}

	public static CachedObjectNode create(int namespaceIndex, String name, int nodeId, CachedObjectTypeNode type) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedObjectNode cbn = new CachedObjectNode(id);
		cbn.setDisplayName(new LocalizedText("en", name));
		cbn.setBrowseName(new QualifiedName(namespaceIndex, name));
		cbn.getReferences()
				.add(new CachedReference("HasTypeDefinition", type.getBrowseName(), "null", type.getNodeId()));

		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(cbn);

		for (CachedBaseNode child : NodeNavigator.getInstance().aggregateInheritedChildren(type)) {
			NodeGenerator.insertNode(child, cbn);
		}

		// rewire references & duplicate children recursive
		return cbn.duplicate();
	}
}
