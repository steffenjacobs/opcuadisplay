package me.steffenjacobs.opcuadisplay.ui.wizard.exp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.ui.wizard.exp.events.ExportWizardCancelEvent;
import me.steffenjacobs.opcuadisplay.ui.wizard.exp.events.ExportWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.ui.wizard.exp.events.ExportWizardOpenEvent;
import me.steffenjacobs.opcuadisplay.ui.wizard.shared.WizardWithUrlAndType;
import me.steffenjacobs.opcuadisplay.ui.wizard.shared.XmlPage;
/** @author Steffen Jacobs */
public class OpcUaExportWizard extends Wizard implements IWorkbenchWizard, WizardWithUrlAndType {

	public XmlPage xmlPage;

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
		xmlPage = new XmlPage("Export model to XML file", "Please enter URI to an XML file to export the model to.",
				false);
		super.addPage(xmlPage);
	}

	@Override
	public boolean performFinish() {
		EventBus.getInstance().fireEvent(new ExportWizardFinishEvent(this.getUrl()));
		return true;
	}

	@Override
	public boolean performCancel() {
		EventBus.getInstance().fireEvent(new ExportWizardCancelEvent());
		return super.performCancel();
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
	}

	@Override
	public String getUrl() {
		return this.exportUrl;
	}

	@Override
	public void setUrl(String url) {
		this.exportUrl = url;
	}

	@Override
	public boolean isType() {
		// type = XML -> false
		return false;
	}
}
