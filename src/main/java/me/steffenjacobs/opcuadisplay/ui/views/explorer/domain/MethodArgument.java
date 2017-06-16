package me.steffenjacobs.opcuadisplay.ui.views.explorer.domain;

import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
/** @author Steffen Jacobs */
public class MethodArgument {
	private final String name;
	private final CachedBaseNode dataTypeNode;

	public MethodArgument(String name, CachedBaseNode dataTypeNode) {
		super();
		this.name = name;
		this.dataTypeNode = dataTypeNode;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataTypeNode == null) ? 0 : dataTypeNode.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MethodArgument other = (MethodArgument) obj;
		if (dataTypeNode == null) {
			if (other.dataTypeNode != null)
				return false;
		} else if (!dataTypeNode.equals(other.dataTypeNode))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public CachedBaseNode getDataTypeNode() {
		return dataTypeNode;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append(" :").append(dataTypeNode);
		return builder.toString();
	}

}