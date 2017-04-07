package me.steffenjacobs.opcuadisplay.shared.domain;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;

public class CachedReference {

	public static final CachedReference PROPERTY_TYPE = new CachedReference("HasTypeDefinition",
			new QualifiedName(0, "PropertyType"), "null", Identifiers.PropertyType);

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CachedReference other = (CachedReference) obj;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}
}
