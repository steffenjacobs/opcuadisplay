package me.steffenjacobs.opcuadisplay.views.reference.domain;

import java.util.ArrayList;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;

public class ReferenceEntryFactory {

	public static Iterable<ReferenceEntry> fromBaseNode(CachedBaseNode node) {
		ArrayList<ReferenceEntry> list = new ArrayList<>();
		if (node == null) {
			return list;
		}

		for (CachedBaseNode ref : node.getReferences()) {
			list.add(new ReferenceEntry("HasTypeDefinition", ref.getNodeId(), ref.getBrowseName(),
					ref.getNodeClass().name(), node));
		}
		return list;
	}

	public static class ReferenceEntry {

		private final String referenceType;
		private final NodeId nodeId;
		private final QualifiedName browseName;
		private final String typeDefinition;
		private final CachedBaseNode cachedNode;

		public ReferenceEntry(String referenceType, NodeId nodeId, QualifiedName browseName, String typeDefinition,
				CachedBaseNode cachedNode) {
			super();
			this.referenceType = referenceType;
			this.nodeId = nodeId;
			this.browseName = browseName;
			this.typeDefinition = typeDefinition;
			this.cachedNode = cachedNode;
		}

		public String getReferenceType() {
			return referenceType;
		}

		public NodeId getNodeId() {
			return nodeId;
		}

		public QualifiedName getBrowseName() {
			return browseName;
		}

		public String getTypeDefinition() {
			return typeDefinition;
		}

		public CachedBaseNode getCachedNode() {
			return cachedNode;
		}
	}
}
