package me.steffenjacobs.opcuadisplay.views.explorer.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;

public class DialogFactory {

	public static enum AddDialogType {
		OBJECT("Object", "Types/ObjectTypes/BaseObjectType"),
		VARIABLE("Variable", "Types/DataTypes/BaseDataType"),
		PROPERTY("Property", "Types/DataTypes/BaseDataType"),
		METHOD("Method", null),
		DATA_TYPE("DataType", null),
		VARIABLE_TYPE("VariableType", "Types/VariableTypes/BaseVariableType"),
		OBJECT_TYPE("ObjectType", null);

		private final String name;
		private final String pathToBaseNode;

		private AddDialogType(String name, String pathToBaseNode) {
			this.name = name;
			this.pathToBaseNode = pathToBaseNode;
		}

		public String getName() {
			return name;
		}

		public String getPathToBaseNode() {
			return pathToBaseNode;
		}
	}

	private static DialogFactory instance;

	private DialogFactory() {
		// singleton
	}

	public static DialogFactory getInstance() {
		if (instance == null) {
			instance = new DialogFactory();
		}
		return instance;
	}

	public TitleAreaDialog createAddDialog(final AddDialogType type, final CachedBaseNode selectedParent) {
		switch (type) {
		case OBJECT:
			return new SimpleAddDialog(new Shell(), type.getName(), type.getPathToBaseNode(), new DialogListener() {
				@Override
				public void onOk(int namespace, String name, int nodeId, CachedBaseNode typeN) {
					NodeGenerator.createAndInsert(type, namespace, name, nodeId, typeN, selectedParent);
				}
			});
		case VARIABLE:
			return new SimpleAddDialog(new Shell(), type.getName(), type.getPathToBaseNode(), new DialogListener() {
				@Override
				public void onOk(int namespace, String name, int nodeId, CachedBaseNode typeN) {
					NodeGenerator.createAndInsert(type, namespace, name, nodeId, typeN, selectedParent);
				}
			});
		case PROPERTY:
			return new SimpleAddDialog(new Shell(), type.getName(), type.getPathToBaseNode(), new DialogListener() {
				@Override
				public void onOk(int namespace, String name, int nodeId, CachedBaseNode typeN) {
					NodeGenerator.createAndInsert(type, namespace, name, nodeId, typeN, selectedParent);
				}
			});
		default:
			return null;
		}
	}

}
