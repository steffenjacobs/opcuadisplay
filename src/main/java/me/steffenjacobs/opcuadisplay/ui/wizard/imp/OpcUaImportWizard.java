package me.steffenjacobs.opcuadisplay.ui.wizard.imp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.ui.wizard.imp.events.ImportWizardCancelEvent;
import me.steffenjacobs.opcuadisplay.ui.wizard.imp.events.ImportWizardFinishEvent;
import me.steffenjacobs.opcuadisplay.ui.wizard.imp.events.ImportWizardOpenEvent;
import me.steffenjacobs.opcuadisplay.ui.wizard.shared.WizardWithUrlAndType;
import me.steffenjacobs.opcuadisplay.ui.wizard.shared.XmlPage;

/** @author Steffen Jacobs */
public class OpcUaImportWizard extends Wizard implements IWorkbenchWizard, WizardWithUrlAndType {

	public ImportTypeSelectionPage selectionPage;
	public XmlPage xmlPage;
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
		xmlPage = new XmlPage("Import model from XML file", "Please enter URI to an XML file to import the model from.",
				true);
		serverPage = new ImportFromServerPage();
		super.addPage(selectionPage);
		super.addPage(xmlPage);
		super.addPage(serverPage);
	}

	@Override
	public boolean performFinish() {
		EventBus.getInstance().fireEvent(new ImportWizardFinishEvent(this.getUrl(), this.isType(),
				xmlPage.isBaseTypesImplicit(), xmlPage.isFreeOpcUaModelerCompatibility()));
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

	@Override
	public void init(IWorkbench arg0, IStructuredSelection arg1) {
	}

	@Override
	public String getUrl() {
		return importUrl;

	}

	@Override
	public void setUrl(String url) {
		this.importUrl = url;
	}
}
