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
	private NodeId refNodeId;

	public CachedReference(String referenceType, QualifiedName browseName, String typeDefinition, NodeId refNodeId) {
		super();
		this.referenceType = referenceType;
		this.browseName = browseName;
		this.typeDefinition = typeDefinition;
		this.refNodeId = refNodeId;
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

	public NodeId getRefNodeId() {
		return refNodeId;
	}
	
	public void setBrowseName(QualifiedName browseName) {
		this.browseName = browseName;
	}
	
	public void setTypeDefinition(String typeDefinition) {
		this.typeDefinition = typeDefinition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((refNodeId == null) ? 0 : refNodeId.hashCode());
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
		if (refNodeId == null) {
			if (other.refNodeId != null)
				return false;
		} else if (!refNodeId.equals(other.refNodeId))
			return false;
		return true;
	}
}
