package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
/** @author Steffen Jacobs */
public class SimpleOpcUaTreeProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof CachedBaseNode) {
			return ((CachedBaseNode) arg0).getChildren();
		}
		return null;
	}

	@Override
	public Object[] getElements(Object arg0) {
		if (arg0 instanceof CachedBaseNode) {
			return ((CachedBaseNode) arg0).getChildren();
		}
		return new CachedBaseNode[] { NodeNavigator.getInstance().getRoot() };
	}

	@Override
	public Object getParent(Object arg0) {
		if (arg0 instanceof CachedBaseNode) {
			return ((CachedBaseNode) arg0).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object arg0) {
		if (arg0 instanceof CachedBaseNode) {
			return ((CachedBaseNode) arg0).hasChildren();
		}
		return false;
	}
}
