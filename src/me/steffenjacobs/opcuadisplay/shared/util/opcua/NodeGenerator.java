package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator.NodeManipulator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.xml.XmlImport;
import me.steffenjacobs.opcuadisplay.views.attribute.events.AttributeModifiedEvent;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory.AddDialogType;
import me.steffenjacobs.opcuadisplay.views.explorer.domain.MethodArgument;
import me.steffenjacobs.opcuadisplay.views.explorer.events.ChangeSelectedNodeEvent;

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
	}

	private void createAndInsertVariableType(int namespaceIndex, String name, int nodeId, CachedVariableTypeNode type,
			CachedBaseNode parent) {
		CachedVariableTypeNode node = CachedVariableTypeNode.create(namespaceIndex, name, nodeId, type);
		insertNode(node, parent);
	}

	private void createAndInsertObjectType(int namespaceIndex, String name, int nodeId, CachedBaseNode parent) {
		CachedObjectTypeNode node = CachedObjectTypeNode.create(namespaceIndex, name, nodeId);
		insertNode(node, parent);
	}

	private void createAndInsertDataType(int namespaceIndex, String name, int nodeId, CachedBaseNode parent) {
		CachedDataTypeNode node = CachedDataTypeNode.create(namespaceIndex, name, nodeId);
		insertNode(node, parent);
	}

	public CachedMethodNode createAndInsertMethod(int nameSpaceIndex, String text, int nodeId, CachedBaseNode parent,
			MethodArgument[] inputArgs, MethodArgument[] outputArgs) {
		CachedMethodNode cmn = CachedMethodNode.create(nameSpaceIndex, text, nodeId, inputArgs, outputArgs);

		insertNode(cmn, parent);
		return cmn;
	}

	private CachedObjectNode createAndInsertObject(int nameSpaceIndex, String text, int nodeId,
			CachedObjectTypeNode type, CachedBaseNode parent) {
		CachedObjectNode con = CachedObjectNode.create(nameSpaceIndex, text, nodeId, type);
		insertNode(con, parent);

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
				type.getBrowseName().getName(), type.getNodeId()));

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
				type.getBrowseName().getName(), type.getNodeId()));

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
		root.setReferences(Lists.newArrayList(f, oo, ot, ov));
		NodeNavigator.getInstance().setRoot(root);
		return root;
	}

	public void generateBaseTypes() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("base.xml");
		NodeNavigator.getInstance().setRoot(XmlImport.getInstance().parseFile(is));
	}

	public void generateFolders() {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("folders.xml");
		NodeNavigator.getInstance().setRoot(XmlImport.getInstance().parseFile(is));
	}

	public void insertNode(CachedBaseNode child, CachedBaseNode parent) {
		parent.addChild(child);
		child.setParent(parent);
		CachedReference ref = getAssociatedReference(child, parent);
		if (ref != null) {
			parent.getReferences().add(ref);
		}

		EventBus.getInstance().fireEvent(new AttributeModifiedEvent(parent));
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

		EventBus.getInstance().fireEvent(new ChangeSelectedNodeEvent(node.getParent(), true));
	}
}
