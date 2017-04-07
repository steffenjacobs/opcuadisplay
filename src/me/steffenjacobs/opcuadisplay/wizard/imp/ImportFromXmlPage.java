package me.steffenjacobs.opcuadisplay.wizard.imp;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ImportFromXmlPage extends WizardPage {

	private Text textUrl;
	private Composite container;

	public ImportFromXmlPage() {
		super("Import model from XML file");
		setTitle("Import model from XML file");
		setDescription("Please enter URI to an XML file to import the model from.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label lblUrl = new Label(container, SWT.NONE);
		lblUrl.setText("XML file location:");
		lblUrl.setToolTipText("Location to import the initial model from");

		textUrl = new Text(container, SWT.BORDER | SWT.SINGLE);
		textUrl.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textUrl.getText().isEmpty()) {
					setPageComplete(true);
					((OpcUaImportWizard) getWizard()).importUrl = getUrl();
				} else {
					setPageComplete(false);
				}
			}

		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textUrl.setLayoutData(gd);

		textUrl.setText(System.getProperty("user.dir"));
		textUrl.setToolTipText("Location to import the initial model from");

		textUrl.setFocus();
		textUrl.setSelection(0, textUrl.getText().length());

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(true);

		((OpcUaImportWizard) getWizard()).importUrl = getUrl();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((OpcUaImportWizard) getWizard()).selectionPage;
	}

	public String getUrl() {
		return textUrl.getText();
	}
}
