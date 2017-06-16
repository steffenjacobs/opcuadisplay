package me.steffenjacobs.opcuadisplay.views.explorer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.views.explorer.NodeClassLabelProvider;
import me.steffenjacobs.opcuadisplay.views.explorer.SimpleOpcUaTreeProvider;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory.DialogListener;

/**
 * This class represents a simple dialog to create ObjectNodes and VariableNodes
 * (+ PropertyNodes). <br>
 * it contains one textfield for the name, one for the namespace and one for the
 * node id. The node id can also be automatically generated. Additionally, a
 * type can be selected based on the <i>pathToTypeNode</i> directory given in
 * the constructor. When the ok button is clicked, the form is validated and the
 * DialogListener given in the constructor is called.
 * 
 * @author Steffen Jacobs
 */
public class SimpleAddDialog extends TitleAreaDialog {

	private Text txtName, txtNameSpace, txtNodeId;
	private TreeViewer viewer;

	private final String title, pathToTypeNode;
	private DialogListener listener;

	public SimpleAddDialog(Shell parentShell, String title, String pathToTypeNode, DialogListener listener) {
		super(parentShell);
		this.title = title;
		this.pathToTypeNode = pathToTypeNode;
		this.listener = listener;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Create " + this.title, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create " + this.title);
		super.setDialogHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createNameSpaceField(container);
		createNameField(container);
		createNodeIdField(container);

		Composite containerBottom = new Composite(area, SWT.NONE);
		containerBottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layoutBottom = new GridLayout(1, false);
		containerBottom.setLayout(layoutBottom);

		if (this.pathToTypeNode != null) {
			createTypeTreeViewer(containerBottom);
		}

		return area;
	}

	/** generate the textfield for the nodeid of the new node */
	private void createNodeIdField(Composite container) {
		// add checkbox that toggles NodeId field
		final Button btn = new Button(container, SWT.CHECK);
		btn.setText("Generate numerical NodeId");
		btn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				txtNodeId.setEnabled(!btn.getSelection());
				if (btn.getSelection()) {
					txtNodeId.setText("" + NodeNavigator.getInstance().getHighestNodeId());
				}
				super.widgetSelected(e);
			}
		});
		btn.setSelection(true);

		// spacer
		new Label(container, SWT.NONE);

		// add NodeId field
		Label label = new Label(container, SWT.NONE);
		label.setText("NodeId:");
		label.setToolTipText("NodeId of the new " + this.title.toLowerCase() + ". Has to be unique.");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		txtNodeId = new Text(container, SWT.BORDER);
		txtNodeId.setLayoutData(gridData);
		txtNodeId.setText("" + (NodeNavigator.getInstance().getHighestNodeId() + 1));

		txtNodeId.setEnabled(false);
	}

	/** generates the textfield for the namespace of the new node */
	private void createNameSpaceField(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Namespace Index:");
		label.setToolTipText("Index associated to the namespace for the new " + this.title.toLowerCase());

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		txtNameSpace = new Text(container, SWT.BORDER);
		txtNameSpace.setLayoutData(gridData);
		txtNameSpace
				.setText("" + NodeNavigator.getInstance().getRoot().getChildren()[0].getNodeId().getNamespaceIndex());

	}

	/** generates the textfield for the name of the new node */
	private void createNameField(Composite container) {
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText("Name:");
		lblName.setToolTipText("Name of the new " + this.title.toLowerCase());

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(gridData);
		txtName.setText("Name");

		txtName.setFocus();
		txtName.setSelection(0, txtName.getText().length());
	}

	/** creates the TreeViewer with the type tree in it */
	private void createTypeTreeViewer(Composite container) {

		viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		viewer.setContentProvider(new SimpleOpcUaTreeProvider());
		CachedBaseNode cbn = CachedBaseNode.createEmptyDummy();

		CachedBaseNode typeNode = NodeNavigator.getInstance().navigateByName(this.pathToTypeNode);
		cbn.addChild(typeNode);
		typeNode.setParent(cbn);

		viewer.setInput(cbn);
		viewer.setLabelProvider(new NodeClassLabelProvider());

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 250;

		viewer.getControl().setLayoutData(gridData);

		viewer.setExpandedElements(cbn.getChildren());
		viewer.setExpandedState(cbn, true);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void okPressed() {
		String name = txtName.getText();

		Integer nameSpaceIndex;
		CachedBaseNode node = null;
		int nextNodeId;

		try {
			nameSpaceIndex = Integer.parseInt(txtNameSpace.getText());
		} catch (NumberFormatException e) {
			this.setMessage("'" + txtNameSpace.getText() + "' is not a valid namespace index.", IMessageProvider.ERROR);
			return;
		}
		try {
			nextNodeId = Integer.parseInt(txtNodeId.getText());
		} catch (NumberFormatException e) {
			this.setMessage("'" + txtNodeId.getText() + "' is not a valid NodeId.", IMessageProvider.ERROR);
			return;
		}

		if (this.pathToTypeNode != null) {
			try {
				node = ((CachedBaseNode) ((IStructuredSelection) viewer.getSelection()).getFirstElement());

			} catch (ClassCastException e) {
				// node would be null -> caught below
			}

			if (node == null) {
				this.setMessage("The type for the " + this.title.toLowerCase() + " to create is not specified.",
						IMessageProvider.ERROR);
				return;
			}
		}

		if (listener != null) {
			listener.onOkSimple(nameSpaceIndex, name, nextNodeId, node);
		}
		super.okPressed();
	}

	protected void setDialogListener(DialogListener listener) {
		this.listener = listener;
	}
}
