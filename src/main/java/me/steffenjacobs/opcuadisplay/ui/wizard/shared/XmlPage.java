package me.steffenjacobs.opcuadisplay.ui.wizard.shared;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/** @author Steffen Jacobs */
public class XmlPage extends WizardPage {

	private Text textUrl;
	private Button checkboxBaseTypesImplicit, checkboxFreeOpcUaModelerCompatibility;
	private Composite container;
	private final boolean isImport, merge;

	/** type: true = import, false = export */
	public XmlPage(String caption, String description, boolean type, boolean merge) {
		super(caption);
		setTitle(caption);
		setDescription(description);
		this.isImport = type;
		this.merge = merge;
	}

	@Override
	public boolean isPageComplete() {
		return ((WizardWithUrlAndType) getWizard()).isType() || isValid();
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		Label lblUrl = new Label(container, SWT.NONE);
		lblUrl.setText("XML file location:");
		lblUrl.setToolTipText("Location of the XML file");

		textUrl = new Text(container, SWT.BORDER | SWT.SINGLE);
		textUrl.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				revalidate();
			}
		});

		textUrl.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				FileDialog dialog;
				if (isImport) {
					dialog = new FileDialog(new Shell(), SWT.OPEN);
				} else {
					dialog = new FileDialog(new Shell(), SWT.SAVE);
				}
				dialog.setFilterNames(new String[]
					{ "XML Files", "Any Files" });
				dialog.setFilterExtensions(new String[]
					{ "*.xml", "*.*" });
				dialog.setFilterPath(System.getProperty("user.dir"));
				dialog.setText("Select an XML file...");
				textUrl.setText(dialog.open());
				revalidate();
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		textUrl.setLayoutData(gd);

		textUrl.setText(System.getProperty("user.home"));
		textUrl.setToolTipText("Location of the XML file - Double click to open file browser.");

		textUrl.setFocus();
		textUrl.setSelection(0, textUrl.getText().length());

		if (!merge) {
			checkboxBaseTypesImplicit = new Button(container, SWT.CHECK);

			checkboxBaseTypesImplicit.setLayoutData(gd);

			checkboxBaseTypesImplicit.setText("Base Types implicit");

			Composite spc = new Composite(container, SWT.NONE);
			spc.setLayoutData(gd);

			checkboxFreeOpcUaModelerCompatibility = new Button(container, SWT.CHECK);
			checkboxFreeOpcUaModelerCompatibility.setLayoutData(gd);

			checkboxFreeOpcUaModelerCompatibility.setText("Free OPC UA Modeler Compatibility");
			checkboxFreeOpcUaModelerCompatibility.setSelection(false);

			checkboxFreeOpcUaModelerCompatibility.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					if (checkboxFreeOpcUaModelerCompatibility.getSelection()) {
						checkboxBaseTypesImplicit.setSelection(true);
					}
				}
			});

			checkboxBaseTypesImplicit.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent arg0) {
					if (!checkboxBaseTypesImplicit.getSelection()) {
						checkboxFreeOpcUaModelerCompatibility.setSelection(false);
					}
				}
			});

		}

		// required to avoid an error in the system
		setControl(container);
		revalidate();

		((WizardWithUrlAndType) getWizard()).setUrl(getUrl());
	}

	/** checks & updates page completeness */
	private void revalidate() {
		this.setPageComplete(this.isPageComplete());
	}

	/**
	 * @return if the file specified in TextField textUrl exists. Also updates
	 *         the url in the parent wizard.
	 */
	private boolean isValid() {
		if (!isImport) {
			// export -> create file if necessary
			((WizardWithUrlAndType) getWizard()).setUrl(getUrl());
			return true;
		}

		File f = new File(textUrl.getText());
		if (f.exists() && !f.isDirectory()) {
			((WizardWithUrlAndType) getWizard()).setUrl(getUrl());
			return true;
		}
		return false;
	}

	public String getUrl() {
		return textUrl.getText();
	}

	public boolean isBaseTypesImplicit() {
		if(checkboxBaseTypesImplicit==null){
			return false;
		}
		return checkboxBaseTypesImplicit.getSelection();
	}

	public boolean isFreeOpcUaModelerCompatibility() {
		if(checkboxFreeOpcUaModelerCompatibility==null){
			return false;
		}
		return checkboxFreeOpcUaModelerCompatibility.getSelection();
	}
}
