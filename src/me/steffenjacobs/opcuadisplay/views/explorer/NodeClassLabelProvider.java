package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.swt.graphics.Image;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.shared.util.Images;

public class NodeClassLabelProvider extends LabelProvider {

	@Override
	public String getText(Object obj) {
		return obj.toString();
	}

	@Override
	public Image getImage(Object obj) {
		if (!(obj instanceof CachedBaseNode)) {
			return null;
		}
		CachedBaseNode cn = (CachedBaseNode) obj;

		if (cn.getNodeClass() == NodeClass.Object) {
			if (isFolder(cn)) {
				return Activator.getImage(Images.ExplorerView.FOLDER.getIdentifier());
			}
			return Activator.getImage(Images.ExplorerView.OBJECT.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.Method) {
			return Activator.getImage(Images.ExplorerView.METHOD.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.ObjectType) {
			return Activator.getImage(Images.ExplorerView.OBJECT_TYPE.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.DataType) {
			return Activator.getImage(Images.ExplorerView.DATA_TYPE.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.ReferenceType) {
			return Activator.getImage(Images.ExplorerView.REFERENCE_TYPE.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.VariableType) {
			return Activator.getImage(Images.ExplorerView.VARIABLE_TYPE.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.Variable) {
			if (cn.getReferences().contains(CachedReference.PROPERTY_TYPE)) {
				return Activator.getImage(Images.ExplorerView.PROPERTY.getIdentifier());
			}
			return Activator.getImage(Images.ExplorerView.VARIABLE.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.DataType) {
			return Activator.getImage(Images.ExplorerView.TYPE.getIdentifier());
		}
		return Activator.getImage(Images.ExplorerView.UNKNOWN.getIdentifier());
	}

	private boolean isFolder(CachedBaseNode cn) {
		return cn.getReferences().stream().filter(ref -> ref.getBrowseName().getName().equals("FolderType"))
				.count() > 0;
	}
}
