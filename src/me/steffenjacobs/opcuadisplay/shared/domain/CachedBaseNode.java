package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;

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
		CachedReference f = new CachedReference("HasTypeDefinition", new QualifiedName(0, "FolderType"), null, null);
		CachedReference oo = new CachedReference("Organizes", new QualifiedName(0, "Objects"), "FolderType", null);
		CachedReference ot = new CachedReference("Organizes", new QualifiedName(0, "Types"), "FolderType", null);
		CachedReference ov = new CachedReference("Organizes", new QualifiedName(0, "Views"), "FolderType", null);
		root.setReferences(Lists.newArrayList(f, oo, ot, ov));
		return root;
	}

	public static CachedBaseNode createEmptyDummy() {
		return new CachedBaseNode("");
	}

	public static CachedBaseNode getRoot() {
		if (root == null) {
			createNewRoot();
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
		if ("Root".equals(text)) {
			nodeId = Identifiers.RootFolder;
			nodeClass = NodeClass.Object;
			browseName = new QualifiedName(0, text);
			description = new LocalizedText("null", "The root of the server address space.");
		} else {
			nodeId = new NodeId(0, 0);
			nodeClass = NodeClass.Unspecified;
			browseName = new QualifiedName(0, "Dummy");
			description = new LocalizedText("null", "This is a dummy node.");
		}
		displayName = new LocalizedText("null", text);
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

	protected CachedBaseNode(NodeId nodeId, NodeClass nodeClass) {
		super();
		children = new ArrayList<>();
		this.nodeId = nodeId;
		this.nodeClass = nodeClass;
		references = new ArrayList<>();
	}

	protected CachedBaseNode(CachedBaseNode cbn) {
		super();
		this.browseName = new QualifiedName(cbn.browseName.getNamespaceIndex(), cbn.browseName.getName());
		this.children = new ArrayList<>();
		for (CachedBaseNode child : cbn.children) {
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

		for (CachedReference ref : cbn.references) {
			for (CachedBaseNode child : this.children) {
				if (child.getBrowseName().equals(ref.getBrowseName())) {
					CachedReference cbnNew = new CachedReference(ref.getReferenceType(), ref.getBrowseName(),
							ref.getTypeDefinition(), child.getNodeId());
					this.references.add(cbnNew);
					break;
				}
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

	public CachedBaseNode[] getChildren() {
		// clean up if necessary
		// TODO: remove hack
		final List<QualifiedName> names = this.getReferences().stream()
				.filter(ref -> ref.getReferenceType().equals("HasTypeDefinition")).map(CachedReference::getBrowseName)
				.collect(Collectors.toList());
		children = children.stream().filter(c -> !names.contains(c.getBrowseName())).collect(Collectors.toList());

		// return children
		CachedBaseNode[] childs = new CachedBaseNode[children.size()];
		childs = children.toArray(childs);
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