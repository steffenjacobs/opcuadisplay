package me.steffenjacobs.opcuadisplay.ui.views.explorer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/** @author Steffen Jacobs */
public class LoadVariablesDialog extends TitleAreaDialog {

	private Text txtUrl;

	private String url;

	public LoadVariablesDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Load Variables", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Load Variables");
		setMessage("Please enter an OPC UA Server URL to load the initial model from.", IMessageProvider.INFORMATION);
		super.setDialogHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createURLField(container);

		return area;
	}

	private void createURLField(Composite container) {
		Label lblURL = new Label(container, SWT.NONE);
		lblURL.setText("OPC Server URL:");
		lblURL.setToolTipText("Location to load the initial model from");

		GridData dataURL = new GridData();
		dataURL.grabExcessHorizontalSpace = true;
		dataURL.horizontalAlignment = GridData.FILL;

		txtUrl = new Text(container, SWT.BORDER);
		txtUrl.setLayoutData(dataURL);
		txtUrl.setText("opc.tcp://192.168.1.181:48400/freeopcua/uamodeler/");

		txtUrl.setFocus();
		txtUrl.setSelection(0, txtUrl.getText().length());
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		url = txtUrl.getText();
		super.okPressed();
	}

	public String getURL() {
		return url;
	}
}
