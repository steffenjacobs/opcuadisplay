package me.steffenjacobs.opcuadisplay.wizard.exp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.wizard.exp.events.ExportWizardCancelEvent;
import me.steffenjacobs.opcuadisplay.wizard.exp.events.ExportWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.wizard.exp.events.ExportWizardOpenEvent;

public class OpcUaExportWizard extends Wizard implements IWorkbenchWizard {

	public ExportToXmlPage xmlPage;

	public String exportUrl;

	public OpcUaExportWizard() {
		super();
		setNeedsProgressMonitor(true);
		EventBus.getInstance().fireEvent(new ExportWizardOpenEvent());
	}

	@Override
	public String getWindowTitle() {
		return "Export OPC UA Model";
	}

	@Override
	public void addPages() {
		xmlPage = new ExportToXmlPage();
		super.addPage(xmlPage);
	}

	@Override
	public boolean performFinish() {
		EventBus.getInstance().fireEvent(new ExportWizardFinishEvent(this.getExportUrl()));
		return true;
	}

	@Override
	public boolean performCancel() {
		EventBus.getInstance().fireEvent(new ExportWizardCancelEvent());
		return super.performCancel();
	}

	public String getExportUrl() {
		return exportUrl;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
	}
}
