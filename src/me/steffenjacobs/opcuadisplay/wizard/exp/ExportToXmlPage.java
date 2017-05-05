package me.steffenjacobs.opcuadisplay.wizard.exp;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ExportToXmlPage extends WizardPage {

	private Text textUrl;
	private Composite container;

	public ExportToXmlPage() {
		super("Export model to XML file");
		setTitle("Exprot model to XML file");
		setDescription("Please enter URI to an XML file to export the model to.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label lblUrl = new Label(container, SWT.NONE);
		lblUrl.setText("XML file location:");
		lblUrl.setToolTipText("Location to export model to");

		textUrl = new Text(container, SWT.BORDER | SWT.SINGLE);
		textUrl.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (!textUrl.getText().isEmpty()) {
					setPageComplete(true);
					((OpcUaExportWizard) getWizard()).exportUrl = getUrl();
				} else {
					setPageComplete(false);
				}
			}

		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textUrl.setLayoutData(gd);

		textUrl.setText(System.getProperty("user.dir"));
		textUrl.setToolTipText("Location to export the model to");

		textUrl.setFocus();
		textUrl.setSelection(0, textUrl.getText().length());

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(true);

		((OpcUaExportWizard) getWizard()).exportUrl = getUrl();
	}

	public String getUrl() {
		return textUrl.getText();
	}
}
