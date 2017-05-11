package me.steffenjacobs.opcuadisplay.wizard.newProject;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class NewProjectSelectAutogenerationPage extends WizardPage {

	/** type: true = import, false = export */
	public NewProjectSelectAutogenerationPage() {
		super("Create new OPC UA Model");
		this.setMessage("Select what parts of the new model should be generated");
	}

	private Button checkboxFolders, checkboxBaseTypes;

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;

		// generate folders
		checkboxFolders = new Button(container, SWT.CHECK);

		// generate base types
		checkboxBaseTypes = new Button(container, SWT.CHECK);
		checkboxBaseTypes.setSelection(true);
		checkboxBaseTypes.setText("Generate BaseTypes");

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		checkboxBaseTypes.setLayoutData(gd);

		// initialize generate-folders checkbox
		checkboxFolders.setSelection(true);

		checkboxFolders.setLayoutData(gd);
		checkboxFolders.setText("Generate Folders");

		checkboxFolders.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (!checkboxFolders.getSelection()) {
					checkboxBaseTypes.setSelection(false);
				}
				checkboxBaseTypes.setEnabled(checkboxFolders.getSelection());
			}
		});

		this.setPageComplete(true);

		super.setControl(container);
	}

	public boolean isGenerateFolders() {
		return checkboxFolders.getSelection();
	}

	public boolean isGenerateBaseTypes() {
		return checkboxBaseTypes.getSelection();
	}
}
