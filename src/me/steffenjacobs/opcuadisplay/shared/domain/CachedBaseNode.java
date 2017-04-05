package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

public class CachedBaseNode {
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

	private static CachedBaseNode noDataDummy, loadingDummy, root;

	public static CachedBaseNode getDummyNoData() {
		if (noDataDummy == null) {
			noDataDummy = new CachedBaseNode("Double click here, to load variables.");
		}
		return noDataDummy;
	}

	public static CachedBaseNode createNewRoot() {
		root = new CachedBaseNode("Root");
		return root;
	}

	public static CachedBaseNode getRoot() {
		if (root == null) {
			root = new CachedBaseNode("Root");
		}
		return root;
	}

	public static CachedBaseNode getDummyLoading() {
		if (loadingDummy == null) {
			loadingDummy = new CachedBaseNode("Loading variables...");
		}
		return loadingDummy;
	}

	public boolean isDummy() {
		return this == noDataDummy || this == loadingDummy;
	}

	public List<CachedReference> getReferences() {
		return references == null ? new ArrayList<CachedReference>() : references;
	}

	public void setReferences(List<CachedReference> references) {
		this.references = references;
	}

	private CachedBaseNode(String text) {
		nodeId = new NodeId(0, 0);
		nodeClass = NodeClass.Unspecified;
		browseName = new QualifiedName(0, "dummy");
		displayName = new LocalizedText("en", text);
		description = new LocalizedText("en", "");
		writeMask = UInteger.valueOf(0);
		userWriteMask = UInteger.valueOf(0);
		children = new ArrayList<>();
		references = new ArrayList<>();
		parent = null;

	}

	public CachedBaseNode(ReferenceDescription descr) {
		children = new ArrayList<>();
		this.nodeId = descr.getNodeId().local().orElse(null);
		this.nodeClass = descr.getNodeClass();
		this.browseName = descr.getBrowseName();
		this.displayName = descr.getDisplayName();
		this.description = null;
		this.writeMask = null;
		this.userWriteMask = null;
		references = new ArrayList<>();
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

	public CachedBaseNode[] getChildren() {
		CachedBaseNode[] childs = new CachedBaseNode[children
				.size()/* + references.size() */];
		childs = children.toArray(childs);

		// // TODO: remove
		// if (references.size() > 0) {
		// for (int j = 0; j < references.size(); j++) {
		// childs[children.size() + j] = references.get(j);
		// }
		// }
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
}