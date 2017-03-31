package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.swt.graphics.Image;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
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
			return Activator.getImage(Images.View.FOLDER.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.Method) {
			return Activator.getImage(Images.View.METHOD.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.Variable) {
			if (cn.hasChildren()) {
				return Activator.getImage(Images.View.FILE_YELLOW.getIdentifier());
			}
			return Activator.getImage(Images.View.FILE.getIdentifier());
		}

		else if (cn.getNodeClass() == NodeClass.VariableType || cn.getNodeClass() == NodeClass.ObjectType
				|| cn.getNodeClass() == NodeClass.DataType || cn.getNodeClass() == NodeClass.ReferenceType) {
			return Activator.getImage(Images.View.TYPE.getIdentifier());
		}
		return Activator.getImage(Images.View.FILE.getIdentifier());
	}
}
