package me.steffenjacobs.opcuadisplay.management.node.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;

/** @author Steffen Jacobs */
public class CachedBaseNode implements Comparable<CachedBaseNode> {
	private final NodeId nodeId;
	private NodeClass nodeClass;
	private QualifiedName browseName;
	private LocalizedText displayName;
	private LocalizedText description;
	private UInteger writeMask;
	private UInteger userWriteMask;
	private List<CachedBaseNode> children;
	private CachedBaseNode parent;

	private List<CachedReference> references;

	private static CachedObjectNode noDataDummy, loadingDummy;

	public static CachedObjectNode getDummyNoData() {
		if (noDataDummy == null) {
			noDataDummy = new CachedObjectNode("Double click here, to load variables.");
		}
		return noDataDummy;
	}

	public static CachedObjectNode createEmptyDummy() {
		return new CachedObjectNode("");
	}

	public static CachedObjectNode getDummyLoading() {
		if (loadingDummy == null) {
			loadingDummy = new CachedObjectNode("Loading variables...");
		}
		return loadingDummy;
	}

	public boolean isDummy() {
		return this == noDataDummy || this == loadingDummy;
	}

	public List<CachedReference> getReferences() {
		if (references == null) {
			references = new ArrayList<>();
		}
		return references;
	}

	public void setReferences(List<CachedReference> references) {
		this.references = references;
	}

	protected CachedBaseNode(String text) {
		nodeId = new NodeId(0, 0);
		nodeClass = NodeClass.Unspecified;
		browseName = new QualifiedName(0, "Dummy");
		description = new LocalizedText("null", "This is a dummy node.");

		displayName = new LocalizedText("null", text);
		writeMask = UInteger.valueOf(0);
		userWriteMask = UInteger.valueOf(0);
		children = new ArrayList<>();
		references = new ArrayList<>();
		parent = null;

	}

	public CachedBaseNode(Node node) throws InterruptedException, ExecutionException {
		super();
		// TOOD: improve parallelization
		children = new ArrayList<>();
		this.nodeId = node.getNodeId().get();
		this.nodeClass = node.getNodeClass().get();
		this.browseName = node.getBrowseName().get();
		this.displayName = node.getDisplayName().get();
		this.description = node.getDescription().get();
		this.writeMask = node.getWriteMask().get();
		this.userWriteMask = node.getUserWriteMask().get();
		references = new ArrayList<>();
	}

	public CachedBaseNode(NodeId nodeId, NodeClass nodeClass) {
		super();
		children = new ArrayList<>();
		this.nodeId = nodeId;
		this.nodeClass = nodeClass;
		this.browseName = new QualifiedName(0, "null");
		this.displayName = new LocalizedText("en", "null");
		this.description = new LocalizedText("en", "null");
		this.writeMask = UInteger.valueOf(0);
		this.userWriteMask = UInteger.valueOf(0);
		references = new ArrayList<>();
	}

	protected CachedBaseNode(CachedBaseNode cbn) {
		super();
		this.browseName = new QualifiedName(cbn.browseName.getNamespaceIndex(), cbn.browseName.getName());
		this.children = new ArrayList<>();
		for (CachedBaseNode child : cbn.children) {

			// don't clone subtypes
			if (!NodeNavigator.getInstance().isInTypesFolder(child)
					&& (child instanceof CachedDataTypeNode || child instanceof CachedObjectTypeNode
							|| child instanceof CachedVariableTypeNode || child instanceof CachedReferenceTypeNode)) {
				continue;
			}

			CachedBaseNode dup = child.duplicate();
			dup.setParent(this);
			this.children.add(dup);
		}
		if (cbn.description != null) {
			this.description = new LocalizedText(cbn.description.getLocale(), cbn.description.getText());
		}
		if (cbn.displayName != null) {
			this.displayName = new LocalizedText(cbn.displayName.getLocale(), cbn.displayName.getText());
		}
		this.nodeClass = cbn.nodeClass;
		this.nodeId = new NodeId(cbn.nodeId.getNamespaceIndex().intValue(),
				NodeNavigator.getInstance().generateNewNodeId());
		this.references = new ArrayList<CachedReference>();

		// rewire references to associated children of the parent, if possible
		for (CachedReference ref : cbn.references) {
			Iterator<CachedBaseNode> it = this.children.iterator();
			boolean found = false;
			while (it.hasNext() && !found) {
				CachedBaseNode child = it.next();
				if (child.getBrowseName().equals(ref.getBrowseName())) {
					CachedReference cbnNew = new CachedReference(ref.getReferenceType(), ref.getBrowseName(),
							ref.getTypeDefinition(), child.getNodeId());
					this.references.add(cbnNew);
					found = true;
				}
			}

			// add reference manually (e.g. type declarations do not have an
			// associated node as a child of the parent), if the ReferenceType
			// is not HasSubType, or the node is in the types folder
			if (!found && (!"HasSubtype".equals(ref.getReferenceType())
					|| NodeNavigator.getInstance().isInTypesFolder(cbn))) {
				this.references.add(ref);
			}
		}
		if (cbn.userWriteMask != null) {
			this.userWriteMask = UInteger.valueOf(cbn.userWriteMask.longValue());
		}
		if (cbn.writeMask != null) {
			this.writeMask = UInteger.valueOf(cbn.writeMask.longValue());
		}
	}

	public CachedBaseNode duplicate() {
		return new CachedBaseNode(this);
	}

	public void setParent(CachedBaseNode parent) {
		this.parent = parent;
	}

	public void addChild(CachedBaseNode node) {
		this.children.add(node);
	}

	public CachedBaseNode getParent() {
		return parent;
	}

	public NodeId getNodeId() {
		return nodeId;
	}

	public NodeClass getNodeClass() {
		return nodeClass;
	}

	public QualifiedName getBrowseName() {
		return browseName;
	}

	public LocalizedText getDisplayName() {
		return displayName;
	}

	public LocalizedText getDescription() {
		return description;
	}

	public UInteger getWriteMask() {
		return writeMask;
	}

	public UInteger getUserWriteMask() {
		return userWriteMask;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public boolean removeChild(CachedBaseNode cbn) {
		return this.children.remove(cbn);
	}

	/** @return a passive copy of the datastructure containing the children */
	public CachedBaseNode[] getChildren() {
		// clean up if necessary
		final List<QualifiedName> names = this.getReferences().stream()
				.filter(ref -> ref.getReferenceType().equals("HasTypeDefinition")).map(CachedReference::getBrowseName)
				.collect(Collectors.toList());
		children = children.stream().filter(c -> !names.contains(c.getBrowseName())).collect(Collectors.toList());

		// return children
		CachedBaseNode[] childs = new CachedBaseNode[children.size()];
		childs = children.toArray(childs);

		// sort children
		Arrays.sort(childs);
		return childs;
	}

	public void setNodeClass(NodeClass nodeClass) {
		this.nodeClass = nodeClass;
	}

	public void setBrowseName(QualifiedName browseName) {
		this.browseName = browseName;
	}

	public void setDisplayName(LocalizedText displayName) {
		this.displayName = displayName;
	}

	public void setDescription(LocalizedText description) {
		this.description = description;
	}

	public void setWriteMask(UInteger writeMask) {
		this.writeMask = writeMask;
	}

	public void setUserWriteMask(UInteger userWriteMask) {
		this.userWriteMask = userWriteMask;
	}

	public void setChildren(List<CachedBaseNode> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return this.displayName.getText();
	}

	public String toString(String indent) {
		StringBuilder builder = new StringBuilder();
		builder.append(indent + "CachedNode [nodeId=").append(nodeId).append(", nodeClass=").append(nodeClass)
				.append(", browseName=").append(browseName).append(", displayName=").append(displayName)
				.append(", description=").append(description).append(", writeMask=").append(writeMask)
				.append(", userWriteMask=").append(userWriteMask).append("]\n");
		for (CachedBaseNode child : children) {
			builder.append(child.toString(indent + "  "));
		}
		return builder.toString();
	}

	/** @return true, if class, browsename and nodeclass are equal */
	public boolean isSimilar(CachedBaseNode cbn) {
		if (cbn.getClass() != this.getClass()) {
			return false;
		}
		if (!this.browseName.equals(cbn.browseName)) {
			return false;
		}

		if (this.nodeClass != cbn.nodeClass) {
			return false;
		}

		return true;
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
		CachedBaseNode other = (CachedBaseNode) obj;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		return true;
	}

	@Override
	public int compareTo(CachedBaseNode o) {
		return this.getDisplayName().getText().toLowerCase().compareTo(o.getDisplayName().getText().toLowerCase());
	}
}
