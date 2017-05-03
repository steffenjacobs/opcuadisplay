package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.IdType;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;

public class NodeNavigator {

	private static NodeNavigator instance;

	private CachedBaseNode root;

	// does not work for multiple clients simultaniously!
	private AtomicInteger highestNodeId = new AtomicInteger(-1);

	private NodeNavigator() {
		// singleton
	}

	public static NodeNavigator getInstance() {
		if (instance == null) {
			instance = new NodeNavigator();
		}
		return instance;
	}

	/** @return the root node */
	public CachedBaseNode getRoot() {
		return root;
	}

	/**
	 * Example: navigateByName("Objects/Types/ObjectTypes/BaseObjectType")
	 * 
	 * @return the node navigated to or null, if the node does not exist
	 */
	public CachedBaseNode navigateByName(String url) {
		String[] path = url.split("/");

		CachedBaseNode node = getRoot();
		for (String p : path) {
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

	public void setRoot(CachedBaseNode newRoot) {
		this.root = newRoot;
	}

	// does not work for multiple clients simultaniously!
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

	// does not work for multiple clients simultaniously!
	public int generateNewNodeId() {
		return highestNodeId.incrementAndGet();
	}

	public void resetHighestNodeId() {
		highestNodeId.set(-1);
	}

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

	public String pathAsString(CachedBaseNode node) {
		StringBuilder sb = new StringBuilder();

		List<CachedBaseNode> names = this.getPath(node);
		names.forEach(n -> {
			sb.append("/");
			sb.append(n);
		});
		return sb.toString();
	}

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
}
