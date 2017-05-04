package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import java.util.ArrayList;
import java.util.List;

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
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.MethodArgument;

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
			createObject(namespaceIndex, name, nodeId, (CachedObjectTypeNode) type, parent);
			break;
		case VARIABLE:
			createVariable(namespaceIndex, name, nodeId, (CachedDataTypeNode) type, parent);
			break;
		case PROPERTY:
			createProperty(namespaceIndex, name, nodeId, (CachedDataTypeNode) type, parent);
			break;
		default:
		}
	}

	public static void createMethod(int nameSpaceIndex, String text, int nodeId, CachedBaseNode parent,
			MethodArgument[] inputArgs, MethodArgument[] outputArgs) {
		CachedMethodNode cmn = CachedMethodNode.create(nameSpaceIndex, text, nodeId, inputArgs, outputArgs);
		parent.addChild(cmn);
		cmn.setParent(parent);
		parent.getReferences().add(new CachedReference("HasComponent", cmn.getBrowseName(), null, cmn.getNodeId()));

		// rerender tree viewer
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent());
	}

	private static void createObject(int nameSpaceIndex, String text, int nodeId, CachedObjectTypeNode type,
			CachedBaseNode parent) {
		CachedObjectNode con = CachedObjectNode.create(nameSpaceIndex, text, nodeId, type);
		parent.addChild(con);
		con.setParent(parent);
		if (NodeNavigator.getInstance().isFolder(parent)) {
			parent.getReferences().add(new CachedReference("Organizes", con.getBrowseName(),
					type.getBrowseName().getName(), type.getNodeId()));
		} else {
			parent.getReferences().add(new CachedReference("HasComponent", con.getBrowseName(),
					con.getBrowseName().getName(), con.getNodeId()));
		}

		// rerender tree viewer
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent());
	}

	private static void createVariable(int namespaceIndex, String name, int nodeId, CachedDataTypeNode type,
			CachedBaseNode parent) {
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
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent());
	}

	public static void createProperty(int namespaceIndex, String name, int nodeId, CachedDataTypeNode type,
			CachedBaseNode parent) {
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
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent());
	}

}
