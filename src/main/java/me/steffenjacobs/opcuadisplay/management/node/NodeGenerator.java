package me.steffenjacobs.opcuadisplay.management.node;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator.NodeManipulator;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.opcInterface.xml.XmlImport;
import me.steffenjacobs.opcuadisplay.ui.views.attribute.events.AttributeModifiedEvent;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.dialogs.DialogFactory.AddDialogType;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.domain.MethodArgument;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.events.ChangeSelectedNodeEvent;

/** @author Steffen Jacobs */
public class NodeGenerator {

	private static NodeGenerator instance;

	private NodeGenerator() {
		// singleton
	}

	public static NodeGenerator getInstance() {
		if (instance == null) {
			instance = new NodeGenerator();
		}
		return instance;
	}

	/** creation of objects, variables and properties */
	public void createAndInsert(AddDialogType addType, int namespaceIndex, String name, int nodeId, CachedBaseNode type,
			CachedBaseNode parent) {

		switch (addType) {
		case OBJECT:
			createAndInsertObject(namespaceIndex, name, nodeId, (CachedObjectTypeNode) type, parent);
			break;
		case VARIABLE:
			createAndInsertVariable(namespaceIndex, name, nodeId, (CachedDataTypeNode) type, parent);
			break;
		case PROPERTY:
			createAndInsertProperty(namespaceIndex, name, nodeId, (CachedDataTypeNode) type, parent);
			break;
		case OBJECT_TYPE:
			createAndInsertObjectType(namespaceIndex, name, nodeId, parent);
			break;
		case DATA_TYPE:
			createAndInsertDataType(namespaceIndex, name, nodeId, parent);
		case VARIABLE_TYPE:
			createAndInsertVariableType(namespaceIndex, name, nodeId, (CachedVariableTypeNode) type, parent);
		default:
		}

		// warning when adding a child to object type
		if (parent.getNodeClass() == NodeClass.ObjectType) {

			Activator.openMessageBoxWarning("Warning",
					"'" + name + "' was added to an ObjectType and will therefore be "
							+ "added to all instances that are of this type.");

			NodeNavigator.getInstance().iterateNodes(NodeNavigator.getInstance().getRoot(), new NodeManipulator() {

				@Override
				public void manipulate(CachedBaseNode cbn) {
					CachedReference ref = NodeNavigator.getInstance().getTypeDefinition(cbn).orElse(null);
					if (ref != null && ref.getRefNodeId().equals(parent.getNodeId())) {
						// add if not already exist
						if (containsNoChildOfName(cbn, name)) {
							createAndInsert(addType, namespaceIndex, name,
									NodeNavigator.getInstance().generateNewNodeId(), type, cbn);
						}
					}
				}
			});
		}

		// warning when adding a child to an object that has a type definition
		// != FolderType
		if (parent.getNodeClass() == NodeClass.Object) {
			CachedReference ref = NodeNavigator.getInstance().getTypeDefinition(parent).orElse(null);

			if (ref != null) {
				CachedBaseNode typeDefNode = NodeNavigator.getInstance().getNodeFromId(ref.getRefNodeId());
				if (typeDefNode != null && !typeDefNode.getNodeId().equals(new NodeId(0, 61))
						&& containsNoChildOfName(typeDefNode, name)) {
					Activator.openMessageBoxWarning("Warning",
							"'" + name + "' was added to an Object that had a type definition assigned to it. "
									+ "It therefore no longer fully complies to this definition.");
				}
			}
		}
	}

	private boolean containsNoChildOfName(CachedBaseNode cbn, String name) {
		return Lists.newArrayList(cbn.getChildren()).stream().filter(c -> c.getDisplayName().getText().equals(name))
				.count() == 0;
	}

	private void createAndInsertVariableType(int namespaceIndex, String name, int nodeId, CachedVariableTypeNode type,
			CachedBaseNode parent) {
		CachedVariableTypeNode node = CachedVariableTypeNode.create(namespaceIndex, name, nodeId, type);
		insertNode(node, parent, false);
	}

	private void createAndInsertObjectType(int namespaceIndex, String name, int nodeId, CachedBaseNode parent) {
		CachedObjectTypeNode node = CachedObjectTypeNode.create(namespaceIndex, name, nodeId);
		insertNode(node, parent, false);
	}

	private void createAndInsertDataType(int namespaceIndex, String name, int nodeId, CachedBaseNode parent) {
		CachedDataTypeNode node = CachedDataTypeNode.create(namespaceIndex, name, nodeId);
		insertNode(node, parent, false);
	}

	public CachedMethodNode createAndInsertMethod(int nameSpaceIndex, String text, int nodeId, CachedBaseNode parent,
			MethodArgument[] inputArgs, MethodArgument[] outputArgs) {
		CachedMethodNode cmn = CachedMethodNode.create(nameSpaceIndex, text, nodeId, inputArgs, outputArgs);

		insertNode(cmn, parent, false);
		return cmn;
	}

	private CachedObjectNode createAndInsertObject(int nameSpaceIndex, String text, int nodeId,
			CachedObjectTypeNode type, CachedBaseNode parent) {
		CachedObjectNode con = CachedObjectNode.create(nameSpaceIndex, text, nodeId, type);
		insertNode(con, parent, false);

		return con;
	}

	private CachedVariableNode createAndInsertVariable(int namespaceIndex, String name, int nodeId,
			CachedDataTypeNode type, CachedBaseNode parent) {
		CachedVariableNode cvn = CachedVariableNode.create(namespaceIndex, name, nodeId, type);

		List<CachedReference> refs = new ArrayList<>();
		CachedBaseNode variableTypeNode = NodeNavigator.getInstance()
				.navigateByName("Root/Types/VariableTypes/BaseVariableType/BaseDataVariableType");
		CachedReference ref = new CachedReference("HasTypeDefinition", variableTypeNode.getBrowseName(), "null",
				variableTypeNode.getNodeId());
		refs.add(ref);
		cvn.setReferences(refs);

		parent.addChild(cvn);
		cvn.setParent(parent);
		parent.getReferences().add(new CachedReference("HasComponent", cvn.getBrowseName(),
				type.getBrowseName().getName(), cvn.getNodeId()));

		// rerender tree viewer
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent(parent));

		return cvn;
	}

	public CachedVariableNode createAndInsertProperty(int namespaceIndex, String name, int nodeId,
			CachedDataTypeNode type, CachedBaseNode parent) {
		CachedVariableNode cvn = CachedVariableNode.create(namespaceIndex, name, nodeId, type);

		List<CachedReference> refs = new ArrayList<>();
		CachedBaseNode variableTypeNode = NodeNavigator.getInstance()
				.navigateByName("Root/Types/VariableTypes/BaseVariableType/PropertyType");
		CachedReference ref = new CachedReference("HasTypeDefinition", variableTypeNode.getBrowseName(), "null",
				variableTypeNode.getNodeId());
		refs.add(ref);
		cvn.setReferences(refs);

		parent.addChild(cvn);
		cvn.setParent(parent);
		parent.getReferences().add(new CachedReference("HasProperty", cvn.getBrowseName(),
				type.getBrowseName().getName(), cvn.getNodeId()));

		// rerender tree viewer
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent(parent));

		return cvn;
	}

	private CachedReference getTypeDefinition(CachedBaseNode node) {
		return Lists.newArrayList(node.getReferences()).stream()
				.filter(r -> "HasTypeDefinition".equals(r.getReferenceType())).findFirst().orElse(null);
	}

	private CachedReference getAssociatedReference(CachedBaseNode child, CachedBaseNode parent) {
		if (NodeNavigator.getInstance().isFolder(parent)) {
			CachedReference typeDefinition = getTypeDefinition(child);
			return new CachedReference("Organizes", child.getBrowseName(),
					typeDefinition != null ? typeDefinition.getBrowseName().getName() : null, child.getNodeId());
		}

		if (NodeNavigator.getInstance().isType(parent) && NodeNavigator.getInstance().isType(child)) {
			return new CachedReference("HasSubtype", child.getBrowseName(), null, child.getNodeId());
		}

		if (child.getNodeClass() == NodeClass.Method || child.getNodeClass() == NodeClass.Object
				|| child.getNodeClass() == NodeClass.Variable) {

		}
		if (child.getNodeClass() == NodeClass.Method) {
			return new CachedReference("HasComponent", child.getBrowseName(), null, child.getNodeId());
		}

		if (child.getNodeClass() == NodeClass.Variable) {
			if (NodeNavigator.getInstance().isProperty(child)) {
				return new CachedReference("HasProperty", child.getBrowseName(), "PropertyType", child.getNodeId());
			}

			CachedReference typeDefinition = getTypeDefinition(child);
			return new CachedReference("HasComponent", child.getBrowseName(),
					typeDefinition != null ? typeDefinition.getBrowseName().getName() : null, child.getNodeId());
		}

		if (child.getNodeClass() == NodeClass.Object) {
			CachedReference typeDefinition = getTypeDefinition(child);
			return new CachedReference("HasComponent", child.getBrowseName(),
					typeDefinition != null ? typeDefinition.getBrowseName().getName() : null, child.getNodeId());
		}
		return null;
	}

	public CachedObjectNode generateRoot() {
		CachedObjectNode root = new CachedObjectNode(Identifiers.RootFolder);

		root.setBrowseName(new QualifiedName(0, "Root"));
		root.setDescription(new LocalizedText("en", "The root of the server address space."));
		root.setDisplayName(new LocalizedText("en", "Root"));
		root.setWriteMask(UInteger.valueOf(0));
		root.setUserWriteMask(UInteger.valueOf(0));
		root.setChildren(new ArrayList<>());
		root.setEventNotifier(UByte.valueOf(0));

		CachedReference f = new CachedReference("HasTypeDefinition", new QualifiedName(0, "FolderType"), "null",
				Identifiers.FolderType);
		CachedReference oo = new CachedReference("Organizes", new QualifiedName(0, "Objects"), "FolderType",
				Identifiers.ObjectsFolder);
		CachedReference ot = new CachedReference("Organizes", new QualifiedName(0, "Types"), "FolderType",
				Identifiers.TypesFolder);
		CachedReference ov = new CachedReference("Organizes", new QualifiedName(0, "Views"), "FolderType",
				Identifiers.ViewsFolder);

		List<CachedReference> references = new CopyOnWriteArrayList<>();
		references.add(f);
		references.add(oo);
		references.add(ot);
		references.add(ov);
		root.setReferences(references);

		NodeNavigator.getInstance().setRoot(root);
		return root;
	}

	public void generateBaseTypes() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("base.xml");
		NodeNavigator.getInstance().setRoot(XmlImport.getInstance().parseFile(is, false, false, false));
	}

	public void generateFolders() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("folders.xml");
		NodeNavigator.getInstance().setRoot(XmlImport.getInstance().parseFile(is, false, false, false));
	}

	public CachedBaseNode mergeInsertNode(CachedBaseNode child, CachedBaseNode parent) {
		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(child);
		List<CachedBaseNode> children = Lists.newArrayList(parent.getChildren());
		int index = children.indexOf(child);
		// copy subchilds to merge existing base structure with loaded xml
		// structure
		if (index > -1) {
			CachedBaseNode containedChild = children.get(index);
			NodeMerger.getInstance().merge(containedChild, child);
			for (CachedBaseNode childOfChild : child.getChildren()) {
				return mergeInsertNode(childOfChild, containedChild);
			}
			return containedChild;
		} else {
			insertNode(child, parent, true);
			return child;
		}
	}

	public void insertNode(CachedBaseNode child, CachedBaseNode parent, boolean suppressEvent) {
		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(child);
		NodeNavigator.getInstance().addNodeToCache(child);
		parent.addChild(child);
		child.setParent(parent);
		CachedReference ref = getAssociatedReference(child, parent);
		if (ref != null && !parent.getReferences().contains(ref)) {
			parent.getReferences().add(ref);
		}

		if (!suppressEvent) {
			EventBus.getInstance().fireEvent(new AttributeModifiedEvent(parent));
		}
	}

	public void removeNode(final CachedBaseNode node) {
		switch (node.getNodeClass()) {
		case Object:
		case Method:
		case Variable:
		case View:
			node.getParent().removeChild(node);
			node.getParent().setReferences(node.getParent().getReferences().stream()
					.filter(ref -> !ref.getRefNodeId().equals(node.getNodeId())).collect(Collectors.toList()));
			break;
		case VariableType:
		case ObjectType:
		case DataType:
			node.getParent().removeChild(node);
			node.getParent().setReferences(node.getParent().getReferences().stream()
					.filter(ref -> !ref.getRefNodeId().equals(node.getNodeId())).collect(Collectors.toList()));

			// get NodeIds of type and subtypes
			final List<NodeId> nodes = NodeNavigator.getInstance().aggregateSubTypes(node).stream()
					.map(CachedBaseNode::getNodeId).collect(Collectors.toList());
			NodeNavigator.getInstance().iterateNodes(NodeNavigator.getInstance().getRoot(), new NodeManipulator() {

				@Override
				public void manipulate(CachedBaseNode cbn) {
					cbn.setReferences(cbn.getReferences().stream()
							.filter(ref -> !("HasTypeDefinition".equals(ref.getReferenceType())
									&& nodes.contains(ref.getRefNodeId())))
							.collect(Collectors.toList()));
				}
			});
			break;
		case ReferenceType:
			node.getParent().removeChild(node);
			node.getParent().setReferences(node.getParent().getReferences().stream()
					.filter(ref -> !ref.getRefNodeId().equals(node.getNodeId())).collect(Collectors.toList()));

			// get names of type and subtypes
			final List<String> nodes2 = NodeNavigator.getInstance().aggregateSubTypes(node).stream()
					.map(CachedBaseNode::getBrowseName).map(QualifiedName::getName).collect(Collectors.toList());

			NodeNavigator.getInstance().iterateNodes(NodeNavigator.getInstance().getRoot(), new NodeManipulator() {
				@Override
				public void manipulate(CachedBaseNode cbn) {
					cbn.setReferences(cbn.getReferences().stream()
							.filter(ref -> !nodes2.contains(ref.getReferenceType())).collect(Collectors.toList()));
				}
			});
			break;
		default:
		}

		// warning when removing a child from object type
		CachedBaseNode parent = node.getParent();
		String name = node.getDisplayName().getText();
		if (parent != null && parent.getNodeClass() == NodeClass.ObjectType) {
			Activator.openMessageBoxWarning("Warning", "'" + name + "' was removed from an ObjectType. "
					+ "Therefore, all instances of this type might no longer fully comply to this definition.");
		}

		// warning when removing a child from an object that has a type
		// definition
		if (parent.getNodeClass() == NodeClass.Object) {
			CachedReference ref = NodeNavigator.getInstance().getTypeDefinition(parent).orElse(null);

			if (ref != null) {
				CachedBaseNode typeDefNode = NodeNavigator.getInstance().getNodeFromId(ref.getRefNodeId());
				if (typeDefNode != null && !containsNoChildOfName(typeDefNode, name)) {
					Activator.openMessageBoxWarning("Warning",
							"'" + name + "' was removed from an Object that had a type definition assigned to it. "
									+ "It therefore no longer fully complies to this definition.");
				}
			}
		}

		EventBus.getInstance().fireEvent(new ChangeSelectedNodeEvent(node.getParent(), true));
	}
}
