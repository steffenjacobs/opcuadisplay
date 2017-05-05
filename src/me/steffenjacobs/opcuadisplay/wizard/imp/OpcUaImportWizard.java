package me.steffenjacobs.opcuadisplay.wizard.imp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.wizard.imp.events.ImportWizardCancelEvent;
import me.steffenjacobs.opcuadisplay.wizard.imp.events.ImportWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.wizard.imp.events.ImportWizardOpenEvent;

public class OpcUaImportWizard extends Wizard implements IWorkbenchWizard {

	public ImportTypeSelectionPage selectionPage;
	public ImportFromXmlPage xmlPage;
	public ImportFromServerPage serverPage;

	public boolean importType;
	public String importUrl;

	public OpcUaImportWizard() {
		super();
		setNeedsProgressMonitor(true);
		EventBus.getInstance().fireEvent(new ImportWizardOpenEvent());
	}

	@Override
	public String getWindowTitle() {
		return "Import OPC UA Model";
	}

	@Override
	public void addPages() {
		selectionPage = new ImportTypeSelectionPage();
		xmlPage = new ImportFromXmlPage();
		serverPage = new ImportFromServerPage();
		super.addPage(selectionPage);
		super.addPage(xmlPage);
		super.addPage(serverPage);
	}

	@Override
	public boolean performFinish() {
		EventBus.getInstance().fireEvent(new ImportWizardFinishEvent(this.getImportUrl(), this.isType()));
		return true;
	}

	@Override
	public boolean performCancel() {
		EventBus.getInstance().fireEvent(new ImportWizardCancelEvent());
		return super.performCancel();
	}

	/**
	 * @return true: when "Import From OPC UA Server" is selected<br>
	 *         false: when "Import From XML" is selected
	 */
	public boolean isType() {
		return importType;
	}

	public String getImportUrl() {
		return importUrl;
	}

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
	}
}
