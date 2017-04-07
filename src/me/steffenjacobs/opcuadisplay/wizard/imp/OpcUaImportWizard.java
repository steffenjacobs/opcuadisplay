package me.steffenjacobs.opcuadisplay.wizard.imp;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class OpcUaImportWizard extends Wizard implements IWorkbenchWizard{

	public ImportTypeSelectionPage selectionPage;
	public ImportFromXmlPage xmlPage;
	public ImportFromServerPage serverPage;

	public boolean importType;
	public String importUrl;

	public OpcUaImportWizard() {
		super();
		setNeedsProgressMonitor(true);
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
		return true;
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
