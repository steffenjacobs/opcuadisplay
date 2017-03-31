package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Shell;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.StandaloneNodeExplorerClient;

public class OpcUaConnector implements ITreeContentProvider {

	private final Shell parentShell;

	public OpcUaConnector(Shell parentShell) {
		this.parentShell = parentShell;
	}

	private CachedBaseNode root;

	public void overwriteRoot(CachedBaseNode newRoot) {
		this.root = newRoot;
	}

	public CachedBaseNode getRoot() {
		return root;
	}

	public void loadVariables(String url) {
		try {
			root = new StandaloneNodeExplorerClient().retrieveNodes(url);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(parentShell, "OPC UA Display", e.getLocalizedMessage());
		}
	}

	@Override
	public Object[] getChildren(Object arg0) {
		if (arg0 instanceof CachedBaseNode) {
			return ((CachedBaseNode) arg0).getChildren();
		}
		return null;
	}

	@Override
	public Object[] getElements(Object arg0) {
		return new CachedBaseNode[] { root };
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
