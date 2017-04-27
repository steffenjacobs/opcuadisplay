package me.steffenjacobs.opcuadisplay.views.explorer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.SharedStorage;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.StandaloneNodeExplorerClient;
import me.steffenjacobs.opcuadisplay.views.explorer.events.RootUpdatedEvent;

public class OpcUaConnector implements ITreeContentProvider {

	private final Shell parentShell;

	public OpcUaConnector(Shell parentShell) {
		this.parentShell = parentShell;
	}

	public void overwriteRoot(CachedBaseNode newRoot) {
		SharedStorage.getInstance().setValue(SharedStorage.SharedField.RootNode, newRoot);
	}

	public void loadVariables(final String url) {
		Job job = new Job("Downloading OPC UA nodes...") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					SharedStorage.getInstance().setValue(SharedStorage.SharedField.RootNode,
							new StandaloneNodeExplorerClient().retrieveNodes(url, monitor));

					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							EventBus.getInstance()
									.fireEvent(new RootUpdatedEvent(SharedStorage.getInstance().getRoot()));
						}
					});
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(parentShell, "OPC UA Display", e.getLocalizedMessage());
						}
					});
					return Status.CANCEL_STATUS;
				}
			}
		};

		job.setUser(true);
		job.schedule();

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
		return new CachedBaseNode[] { SharedStorage.getInstance().getRoot() };
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
