package me.steffenjacobs.opcuadisplay.ui.views.explorer;

import org.eclipse.jface.viewers.ITreeContentProvider;

import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
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
