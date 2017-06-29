package me.steffenjacobs.opcuadisplay.ui.views.explorer.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;

import me.steffenjacobs.opcuadisplay.management.node.NodeGenerator;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.domain.MethodArgument;

/**
 * Dialog factory for the add-dialogs
 * 
 * @author Steffen Jacobs
 */
public class DialogFactory {

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

	/** Type of nodes, a specific node-creation-dialog can create */
	public static enum AddDialogType {
		OBJECT("Object", "Types/ObjectTypes/BaseObjectType"),
		VARIABLE("Variable", "Types/DataTypes/BaseDataType"),
		PROPERTY("Property", "Types/DataTypes/BaseDataType"),
		METHOD("Method", null),
		DATA_TYPE("DataType", null),
		VARIABLE_TYPE("VariableType", "Types/VariableTypes/BaseVariableType"),
		OBJECT_TYPE("ObjectType", null);

		private final String name;
		private final String pathToBaseTypeNode;

		private AddDialogType(String name, String pathToBaseTypeNode) {
			this.name = name;
			this.pathToBaseTypeNode = pathToBaseTypeNode;
		}

		public String getName() {
			return name;
		}

		public String getPathToBaseTypeNode() {
			return pathToBaseTypeNode;
		}
	}

	/**
	 * Dialog listener for SimpleAddDialog. Is called, when the OK-button is
	 * pressed and the validation had been successful.
	 */
	public static abstract class DialogListener {

		public void onOkSimple(int namespace, String name, int nodeId, CachedBaseNode type) {
		};

		public void onOkMethod(int namespace, String name, int nodeId, MethodArgument[] inputArgs,
				MethodArgument[] outputArgs) {
		};

	}

	/**
	 * @return a node creation dialog for the given AddDialogType <i>type</i>.
	 *         The node will then added as a child to <selectedParent>
	 */
	public TitleAreaDialog createAddDialog(final AddDialogType type, final CachedBaseNode selectedParent) {
		switch (type) {
		case OBJECT:
		case VARIABLE:
		case VARIABLE_TYPE:
		case PROPERTY:
		case DATA_TYPE:
		case OBJECT_TYPE:
			return new SimpleAddDialog(new Shell(), type.getName(), type.getPathToBaseTypeNode(), new DialogListener() {
				@Override
				public void onOkSimple(int namespace, String name, int nodeId, CachedBaseNode typeN) {
					NodeGenerator.getInstance().createAndInsert(type, namespace, name, nodeId, typeN, selectedParent);
				}
			});
		case METHOD:
			return new MethodAddDialog(new Shell(), type.getName(), new DialogListener() {
				@Override
				public void onOkMethod(int namespace, String name, int nodeId, MethodArgument[] inputArgs,
						MethodArgument[] outputArgs) {
					NodeGenerator.getInstance().createAndInsertMethod(namespace, name, nodeId, selectedParent,
							inputArgs, outputArgs);
				}
			});

		default:
			return null;
		}
	}
}
