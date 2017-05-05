package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.views.attribute.events.AttributeModifiedEvent;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory.AddDialogType;
import me.steffenjacobs.opcuadisplay.views.explorer.domain.MethodArgument;

public class NodeGenerator {

	private static NodeGenerator instance;

	private NodeGenerator() {
		// singleton
	}

	public NodeGenerator getInstance() {
		if (instance == null) {
			instance = new NodeGenerator();
		}
		return instance;
	}

	/** creation of objects, variables and properties */
	public static void createAndInsert(AddDialogType addType, int namespaceIndex, String name, int nodeId,
			CachedBaseNode type, CachedBaseNode parent) {

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
		default:
		}
	}
	private static void createAndInsertObjectType(int namespaceIndex, String name, int nodeId, CachedBaseNode parent) {
		CachedObjectTypeNode node = CachedObjectTypeNode.create(namespaceIndex, name, nodeId);
		insertNode(node, parent);
	}
	
	private static void createAndInsertDataType(int namespaceIndex, String name, int nodeId, CachedBaseNode parent) {
		CachedDataTypeNode node = CachedDataTypeNode.create(namespaceIndex, name, nodeId);
		insertNode(node, parent);
	}

	public static CachedMethodNode createAndInsertMethod(int nameSpaceIndex, String text, int nodeId,
			CachedBaseNode parent, MethodArgument[] inputArgs, MethodArgument[] outputArgs) {
		CachedMethodNode cmn = CachedMethodNode.create(nameSpaceIndex, text, nodeId, inputArgs, outputArgs);

		insertNode(cmn, parent);
		return cmn;
	}

	private static CachedObjectNode createAndInsertObject(int nameSpaceIndex, String text, int nodeId,
			CachedObjectTypeNode type, CachedBaseNode parent) {
		CachedObjectNode con = CachedObjectNode.create(nameSpaceIndex, text, nodeId, type);
		insertNode(con, parent);

		return con;
	}

	private static CachedVariableNode createAndInsertVariable(int namespaceIndex, String name, int nodeId,
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

	public static CachedVariableNode createAndInsertProperty(int namespaceIndex, String name, int nodeId,
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

	private static CachedReference getTypeDefinition(CachedBaseNode node) {
		return Lists.newArrayList(node.getReferences()).stream()
				.filter(r -> "HasTypeDefinition".equals(r.getReferenceType())).findFirst().orElse(null);
	}

	private static CachedReference getAssociatedReference(CachedBaseNode child, CachedBaseNode parent) {
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

	public static void insertNode(CachedBaseNode child, CachedBaseNode parent) {
		parent.addChild(child);
		child.setParent(parent);
		CachedReference ref = getAssociatedReference(child, parent);
		if (ref != null) {
			parent.getReferences().add(ref);
		}

		EventBus.getInstance().fireEvent(new AttributeModifiedEvent(parent));
	}
}
