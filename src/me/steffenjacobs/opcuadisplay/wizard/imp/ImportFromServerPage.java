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

public class ImportFromServerPage extends WizardPage {

	private Text textUrl;
	private Composite container;

	public ImportFromServerPage() {
		super("Import model from OPC UA Server");
		setTitle("Import model from OPC UA Server");
		setDescription("Please enter an OPC UA Server URL to import the model from.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label lblUrl = new Label(container, SWT.NONE);
		lblUrl.setText("OPC Server URL:");
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

		textUrl.setText("opc.tcp://192.168.1.181:48400/freeopcua/uamodeler/");
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
