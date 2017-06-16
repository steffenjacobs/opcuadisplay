package me.steffenjacobs.opcuadisplay.ui.wizard.imp;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
/** @author Steffen Jacobs */
public class ImportTypeSelectionPage extends WizardPage {

	public ImportTypeSelectionPage() {
		super("Select Import Method");
		setTitle("Select Import Method");
		setDescription("Please select the import method.");
	}

	private Button[] radios = new Button[3];

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);

		radios[0] = new Button(container, SWT.RADIO);
		radios[0].setSelection(true);
		radios[0].setText("Import From OPC UA Server");
		radios[0].setBounds(10, 5, 250, 30);
		radios[0].setSelection(true);

		radios[1] = new Button(container, SWT.RADIO);
		radios[1].setText("Import From XML");
		radios[1].setBounds(10, 30, 250, 30);
		setControl(container);
		setPageComplete(true);
	}

	/**
	 * @return true: when "Import From OPC UA Server" is selected<br>
	 *         false: when "Import From XML" is selected
	 */
	public boolean getImportType() {
		return radios[0].getSelection();
	}

	@Override
	public IWizardPage getNextPage() {
		((OpcUaImportWizard) getWizard()).importType = getImportType();
		if (getImportType()) {
			return ((OpcUaImportWizard) getWizard()).serverPage;
		} else {
			return ((OpcUaImportWizard) getWizard()).xmlPage;
		}
	}
}
