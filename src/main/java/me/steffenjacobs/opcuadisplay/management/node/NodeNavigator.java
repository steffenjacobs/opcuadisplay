package me.steffenjacobs.opcuadisplay.management.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.IdType;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.events.SelectedNodeChangedEvent;

/**
 * Transforms the node tree without generating new nodes, handles the root node
 * and can be queried for nodes or the node id.
 * 
 * @author Steffen Jacobs
 */
public class NodeNavigator {

	public static interface NodeManipulator {
		void manipulate(CachedBaseNode cbn);
	}

	private static final String[] HIERARCHICAL_REFERENCES = new String[]
		{ "HasChild", "Aggregates", "HasComponent", "HasOrderedComponent", "HasHistoricalConfiguration", "HasProperty",
				"HasSubtype", "HasEventSource", "HasNotifier", "Organizes" };

	private static NodeNavigator instance;

	private CachedBaseNode selectedNode;

	private CachedObjectNode root, cachedRoot;

	private Map<NodeId, CachedBaseNode> nodeCache;
	private boolean cacheValid = false;

	// does not work for multiple clients simultaniously!
	private AtomicInteger highestNodeId = new AtomicInteger(-1);

	private NodeNavigator() {
		// singleton
		EventBus.getInstance().addListener("dummy", SelectedNodeChangedEvent.IDENTIFIER,
				new EventListener<SelectedNodeChangedEvent>() {

					@Override
					public void onAction(SelectedNodeChangedEvent event) {
						selectedNode = event.getNode();
					}
				});
	}

	public static NodeNavigator getInstance() {
		if (instance == null) {
			instance = new NodeNavigator();
		}
		return instance;
	}

	/** @return the root node */
	public CachedObjectNode getRoot() {
		return root;
	}

	/**
	 * Example: navigateByName("Objects/Types/ObjectTypes/BaseObjectType")
	 * 
	 * @return the node navigated to or null, if the node does not exist
	 */
	public CachedBaseNode navigateByName(String url) {
		return navigateByName(url, getRoot());
	}

	public CachedBaseNode navigateByName(String url, CachedBaseNode node) {
		String[] path = url.split("/");

		for (String p : path) {
			if (p == path[0] && p.equals("Root")) {
				continue;
			}
			final CachedBaseNode orig = node;
			for (CachedBaseNode cbn : node.getChildren()) {
				if (cbn.getBrowseName().getName().equals(p)) {
					node = cbn;
					break;
				}
			}
			if (orig == node) {
				return null;
			}
		}
		return node;
	}

	/** sets the root node to <i>newRoot</i> */
	public void setRoot(CachedObjectNode newRoot) {
		this.root = newRoot;
		cacheValid = false;
	}

	/**
	 * increases the highest node id if necessary based on the node id of
	 * <i>cn</i> <br>
	 * Attention: does not work for multiple clients simultaneously!
	 */
	public void increaseHighestNodeIdIfNecessarySafe(CachedBaseNode cn) {

		if (cn == null || cn.getNodeId() == null || cn.getNodeId().getIdentifier() == null
				|| cn.getNodeId().getType() != IdType.Numeric) {
			return;
		}
		final int nodeId;
		if (cn.getNodeId().getIdentifier() instanceof UInteger) {
			nodeId = ((UInteger) cn.getNodeId().getIdentifier()).intValue();
		} else if (cn.getNodeId().getIdentifier() instanceof Integer) {
			nodeId = (int) cn.getNodeId().getIdentifier();
		} else {
			return;
		}

		highestNodeId.getAndUpdate(x -> x = x > nodeId ? x : nodeId);
	}

	/**
	 * generated a new node id<br>
	 * Attention: does not work for multiple clients simultaneously!
	 */
	public int generateNewNodeId() {
		return highestNodeId.incrementAndGet();
	}

	/** resets the highest node id to -1 */
	public void resetHighestNodeId() {
		highestNodeId.set(-1);
	}

	/** @return the highest node id without incrementing */
	public int getHighestNodeId() {
		return highestNodeId.get();
	}

	/**
	 * @return true, if <i>node</i> is in the Types folder. <br>
	 *         false, if <i>node</i> is the Types folder or not in the Types
	 *         folder.
	 */
	public boolean isInTypesFolder(CachedBaseNode node) {

		while ((node = node.getParent()) != null) {
			if (Identifiers.TypesFolder.equals(node.getNodeId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return true, if <i>node</i> is cyclic by checking, if the BrowseName is
	 *         already present in the tree.
	 */
	public boolean isCyclic(CachedBaseNode node) {
		List<QualifiedName> path = new ArrayList<>();
		path.add(node.getBrowseName());
		while ((node = node.getParent()) != null) {
			if (path.contains(node.getBrowseName())) {
				return true;
			}
			path.add(node.getBrowseName());
		}

		return false;
	}

	/** @return the path from the Root node to <i>node</i> as a String */
	public String pathAsString(CachedBaseNode node) {
		StringBuilder sb = new StringBuilder();

		List<CachedBaseNode> names = this.getPath(node);
		names = Lists.reverse(names);
		names.forEach(n -> {
			sb.append("/");
			sb.append(n.getBrowseName().getName());
		});
		return sb.toString();
	}

	/** @return all nodes on the path from the Root node to <i>node</i> */
	public List<CachedBaseNode> getPath(CachedBaseNode node) {
		Stack<CachedBaseNode> path = new Stack<>();
		if (node == null) {
			return path;
		}
		path.add(node);
		while ((node = node.getParent()) != null) {
			final CachedBaseNode nc = node;
			if (path.stream().filter(n -> n.isSimilar(nc)).count() > 0) {
				CachedBaseNode cbn = CachedBaseNode.createEmptyDummy();
				cbn.setBrowseName(new QualifiedName(0, "CYCLIC"));
				cbn.setDisplayName(new LocalizedText("null", "CYCLIC"));
				path.add(cbn);
				return path;
			}
			path.add(nc);
		}
		return path;
	}

	/** @return true: if <i>cn</i> has a type definition linked to FolderType */
	public boolean isFolder(CachedBaseNode cn) {
		return cn.getReferences().stream().filter(ref -> ref.getReferenceType().equals("HasTypeDefinition")
				&& ref.getBrowseName().getName().equals("FolderType")).count() > 0;
	}

	/** @return true: if <i>cn</i> has a reference to PropertyType */
	public boolean isProperty(CachedBaseNode cn) {
		return cn.getReferences().contains(CachedReference.PROPERTY_TYPE);
	}

	/**
	 * @return true: if <i>cbn</i> is a 'DataType', 'ObjectType', 'VariableType'
	 *         or ReferenceType'
	 */
	public boolean isType(CachedBaseNode cbn) {
		switch (cbn.getNodeClass()) {
		case DataType:
		case ObjectType:
		case ReferenceType:
		case VariableType:
			return true;
		case Unspecified:
		case Method:
		case Object:
		case Variable:
		case View:
		default:
			return false;
		}
	}

	/** @return true: if <i>referenceType</i> is a hierarchical reference */
	public boolean isHierarchicalReference(String referenceType) {

		for (String s : HIERARCHICAL_REFERENCES) {
			if (s.equals(referenceType)) {
				return true;
			}
		}
		return false;
	}

	/** executes <i>nm</i> for all nodes beneath <i>parent</i> */
	public void iterateNodes(CachedBaseNode parent, NodeManipulator nm) {
		nm.manipulate(parent);
		for (CachedBaseNode cbn : parent.getChildren()) {
			iterateNodes(cbn, nm);
		}
	}

	/**
	 * @return a list of all children (objects, methods, variables), that are
	 *         inherited to the subtype <i>typeNode</i>
	 */
	public List<CachedBaseNode> aggregateInheritedChildren(CachedBaseNode typeNode) {
		List<CachedBaseNode> result = new ArrayList<>();

		do {
			result.addAll(Lists.newArrayList(typeNode.getChildren()).stream().filter(c -> !isType(c))
					.collect(Collectors.toList()));
		} while (isType(typeNode = typeNode.getParent()));

		return result;
	}

	/** @return all sub types of <i>node</i> */
	public List<CachedBaseNode> aggregateSubTypes(CachedBaseNode node) {
		List<CachedBaseNode> result = new ArrayList<>();
		result.add(node);

		for (CachedBaseNode c : node.getChildren()) {
			if (isType(c)) {
				result.addAll(aggregateSubTypes(c));
			}
		}
		return result;
	}

	/** @return the type definition of <i>refNode</i> */
	public Optional<CachedReference> getTypeDefinition(CachedBaseNode refNode) {
		return refNode.getReferences().stream().filter(ref -> ref.getReferenceType().equals("HasTypeDefinition"))
				.findAny();
	}

	/** @return the currently selected node */
	public CachedBaseNode getSelectedNode() {
		return this.selectedNode;
	}

	/** @return the CachedBaseNode associated with the NodeId <i>id</i> */
	public CachedBaseNode getNodeFromId(NodeId id) {
		if (!cacheValid) {
			nodeCache = new HashMap<>();

			iterateNodes(root, new NodeManipulator() {

				@Override
				public void manipulate(CachedBaseNode cbn) {
					nodeCache.put(cbn.getNodeId(), cbn);
				}
			});
			cacheValid = true;
		}

		return nodeCache.get(id);

	}

	/** adds <i>child</i> to the cache */
	public void addNodeToCache(CachedBaseNode child) {
		if (cacheValid) {
			nodeCache.put(child.getNodeId(), child);
		}
	}

	/** stores the root */
	public void cacheRoot() {
		this.cachedRoot = this.root;
	}

	/** loads the root from storage */
	public void uncacheRoot() {
		this.root = this.cachedRoot;
	}
}
