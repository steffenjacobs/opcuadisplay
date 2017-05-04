package me.steffenjacobs.opcuadisplay.views.explorer.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
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
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.views.explorer.NodeClassLabelProvider;
import me.steffenjacobs.opcuadisplay.views.explorer.SimpleOpcUaTreeProvider;

public class AddObjectDialog extends TitleAreaDialog {

	private Text txtName, txtNameSpace, txtNodeId;
	private TreeViewer viewer;

	private CachedBaseNode base;

	public AddObjectDialog(Shell parentShell, CachedBaseNode base) {
		super(parentShell);
		this.base = base;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Create Object", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create Object");
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

		createObjectTypeTree(containerBottom);

		return area;
	}

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
		label.setToolTipText("NodeId of the new object. Has to be unique.");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		txtNodeId = new Text(container, SWT.BORDER);
		txtNodeId.setLayoutData(gridData);
		txtNodeId.setText("" + (NodeNavigator.getInstance().getHighestNodeId() + 1));

		txtNodeId.setFocus();
		txtNodeId.setSelection(0, txtNodeId.getText().length());
		txtNodeId.setEnabled(false);
	}

	private void createNameSpaceField(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Namespace Index:");
		label.setToolTipText("Index associated to the namespace for the new object");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		txtNameSpace = new Text(container, SWT.BORDER);
		txtNameSpace.setLayoutData(gridData);
		txtNameSpace
				.setText("" + NodeNavigator.getInstance().getRoot().getChildren()[0].getNodeId().getNamespaceIndex());

		txtNameSpace.setFocus();
		txtNameSpace.setSelection(0, txtNameSpace.getText().length());
	}

	private void createNameField(Composite container) {
		Label lblName = new Label(container, SWT.NONE);
		lblName.setText("Name:");
		lblName.setToolTipText("Name of the new object");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;

		txtName = new Text(container, SWT.BORDER);
		txtName.setLayoutData(gridData);
		txtName.setText("Name");

		txtName.setFocus();
		txtName.setSelection(0, txtName.getText().length());
	}

	private void createObjectTypeTree(Composite container) {

		viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		viewer.setContentProvider(new SimpleOpcUaTreeProvider());
		CachedBaseNode cbn = CachedBaseNode.createEmptyDummy();

		CachedBaseNode typeNode = NodeNavigator.getInstance().navigateByName("Types/ObjectTypes/BaseObjectType");
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
		Integer nameSpaceIndex;
		String name = txtName.getText();

		CachedObjectTypeNode node;
		int nextNodeId;
		try {
			nameSpaceIndex = Integer.parseInt(txtNameSpace.getText());
			nextNodeId = Integer.parseInt(txtNodeId.getText());
			node = ((CachedObjectTypeNode) ((IStructuredSelection) viewer.getSelection()).getFirstElement());
		} catch (ClassCastException cce) {
			return;
		}

		NodeGenerator.createAndInsert(nameSpaceIndex, name, nextNodeId, node, base);
		super.okPressed();
	}
}
