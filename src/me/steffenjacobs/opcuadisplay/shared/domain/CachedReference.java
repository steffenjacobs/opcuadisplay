package me.steffenjacobs.opcuadisplay.shared.domain;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;

public class CachedReference {

	private String referenceType;
	private QualifiedName browseName;
	private String typeDefinition;
	private NodeId nodeId;

	public CachedReference(String referenceType, QualifiedName browseName, String typeDefinition, NodeId nodeId) {
		super();
		this.referenceType = referenceType;
		this.browseName = browseName;
		this.typeDefinition = typeDefinition;
		this.nodeId = nodeId;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public QualifiedName getBrowseName() {
		return browseName;
	}

	public String getTypeDefinition() {
		return typeDefinition;
	}

	public NodeId getNodeId() {
		return nodeId;
	}
}
