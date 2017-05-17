package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
/** @author Steffen Jacobs */
public class ConstraintChecker {

	private static ConstraintChecker instance;

	public static ConstraintChecker getIntsance() {
		if (instance == null) {
			instance = new ConstraintChecker();
		}
		return instance;
	}

	private ConstraintChecker() {
		// singleton
	}

	// node cannot be root folder, a folder directly under the root or a folder
	// directly under the Types folder
	public boolean isRemovalAllowed(CachedBaseNode node) {
		return !NodeNavigator.getInstance().getRoot().equals(node) && !NodeNavigator.getInstance().getRoot().equals(node.getParent())
				&& node.getParent() != null && !Identifiers.TypesFolder.equals(node.getParent().getNodeId());
	}

	// node cannot be root node or directly in types folder or a variable
	public boolean isAddObjectAllowed(CachedBaseNode node) {
		return !NodeNavigator.getInstance().getRoot().equals(node) && !Identifiers.TypesFolder.equals(node.getParent())
				&& !NodeClass.Variable.equals(node.getNodeClass());
	}

	//
	public boolean isAddVariableAllowed(CachedBaseNode node) {
		return isAddObjectAllowed(node);
	}

	//
	public boolean isAddPropertyAllowed(CachedBaseNode node) {
		return isAddObjectAllowed(node);
		
	}

	// node cannot be root node or directly in types folder or a variable
	public boolean isAddMethodAllowed(CachedBaseNode node) {
		return !NodeNavigator.getInstance().getRoot().equals(node) && !Identifiers.TypesFolder.equals(node.getParent())
				&& !NodeClass.Variable.equals(node.getNodeClass());
	}

	// object types can only be added to existing ObjectTypes
	public boolean isAddObjectTypeAllowed(CachedBaseNode node) {
		return NodeClass.ObjectType.equals(node.getNodeClass());
	}

	// variable types can only be added to existing VariableTypes
	public boolean isAddVariableTypeAllowed(CachedBaseNode node) {
		return NodeClass.VariableType.equals(node.getNodeClass());
	}

	// data types can only be added to existing DataTypes
	public boolean isAddDataTypeAllowed(CachedBaseNode node) {
		return NodeClass.DataType.equals(node.getNodeClass());
	}
}
