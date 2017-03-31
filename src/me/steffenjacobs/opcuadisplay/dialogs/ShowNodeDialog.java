package me.steffenjacobs.opcuadisplay.dialogs;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.widgets.DropDownCheckedListBox;
import me.steffenjacobs.opcuadisplay.shared.widgets.GenericComboBox;
import me.steffenjacobs.opcuadisplay.shared.widgets.GenericComboBox.Renderer;
import me.steffenjacobs.opcuadisplay.views.attribute.BitmaskParser;

public class ShowNodeDialog extends TitleAreaDialog {

	private Text txtNodeId, txtBrowseName, txtDisplayName, txtDescription, txtWriteMask, txtUserWriteMask, txtParent;
	private Label lblDescription, lblDisplayName;

	private GenericComboBox<NodeClass> cmbNodeClass;

	private Button okButton;

	private CachedBaseNode displayedNode;

	public ShowNodeDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Show Node Details");
		super.setDialogHelpAvailable(false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumWidth = 200;
		container.setLayoutData(gridData);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		createView(container);

		return area;
	}

	public void setDisplayedNode(CachedBaseNode node) {
		txtBrowseName.setText(node.getBrowseName().getName());

		lblDescription.setText(String.format("Description [%s]: ", node.getDescription().getLocale()));
		txtDescription.setText(node.getDescription().getText() == null ? "" : node.getDescription().getText());

		lblDisplayName.setText(String.format("Display Name [%s]: ", node.getDisplayName().getLocale()));
		txtDisplayName.setText(node.getDisplayName().getText());

		cmbNodeClass.setSelected(node.getNodeClass());

		txtNodeId.setText("" + node.getNodeId().getIdentifier());
		txtParent.setText((node.getParent() == null || node.getParent().getDisplayName() == null) ? ""
				: node.getParent().getDisplayName().getText());
		txtUserWriteMask.setText("" + node.getUserWriteMask());
		txtWriteMask.setText("" + node.getWriteMask());

		// TODO: maybe show children and navigate to sub-dialog?

//		this.setEditable(node.isEditable());
		this.displayedNode = node;
	}

	public CachedBaseNode getDisplayedNode() {
		return displayedNode;
	}

	public void setEditable(boolean enabled) {
		cmbNodeClass.setEnabled(enabled);
		txtBrowseName.setEnabled(enabled);
		txtDisplayName.setEnabled(enabled);
		txtDescription.setEnabled(enabled);
		txtWriteMask.setEnabled(enabled);
		txtUserWriteMask.setEnabled(enabled);
		txtParent.setEnabled(enabled);

		okButton.setVisible(enabled);
	}

	private void createView(Composite container) {

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;

		Label lblNodeId = new Label(container, SWT.NONE);
		lblNodeId.setText("NodeID:");
		lblNodeId.setToolTipText("The NodeID of the selected node");
		txtNodeId = new Text(container, SWT.BORDER);
		txtNodeId.setLayoutData(data);
		txtNodeId.setEnabled(false);

		Label lblNodeClass = new Label(container, SWT.NONE);
		lblNodeClass.setText("Node Class:");
		lblNodeClass.setToolTipText("The type of the selected node");
		cmbNodeClass = new GenericComboBox<NodeClass>(container, SWT.BORDER, new Renderer<NodeClass>() {

			@Override
			public String render(NodeClass obj) {
				return obj.name();
			}
		});
		cmbNodeClass.setItems(NodeClass.values());

		Label lblBrowseName = new Label(container, SWT.NONE);
		lblBrowseName.setText("Browse Name:");
		lblBrowseName.setToolTipText("The internal name of the selected node");
		txtBrowseName = new Text(container, SWT.BORDER);
		txtBrowseName.setLayoutData(data);

		lblDisplayName = new Label(container, SWT.NONE);
		lblDisplayName.setText("Display Name:");
		lblDisplayName.setToolTipText("The display name of the selected node");
		txtDisplayName = new Text(container, SWT.BORDER);
		txtDisplayName.setLayoutData(data);

		lblDescription = new Label(container, SWT.NONE);
		lblDescription.setText("Description:");
		lblDescription.setToolTipText("The description for the selected node");
		txtDescription = new Text(container, SWT.BORDER);
		txtDescription.setLayoutData(data);

		Label lblWriteMask = new Label(container, SWT.NONE);
		lblWriteMask.setText("Write Mask:");
		lblWriteMask.setToolTipText("The write mask of the selected node");
		txtWriteMask = new Text(container, SWT.BORDER);
		txtWriteMask.setLayoutData(data);

		Label lblUserWriteMask = new Label(container, SWT.NONE);
		lblUserWriteMask.setText("User Write Mask:");
		lblUserWriteMask.setToolTipText("The user write mask of the selected node");
		txtUserWriteMask = new Text(container, SWT.BORDER);
		txtUserWriteMask.setLayoutData(data);

		Label lblParent = new Label(container, SWT.NONE);
		lblParent.setText("Parent Name:");
		lblParent.setToolTipText("The parent of the selected node");
		txtParent = new Text(container, SWT.BORDER);
		txtParent.setLayoutData(data);

		DropDownCheckedListBox box = new DropDownCheckedListBox(container);
		box.setMenuValues(BitmaskParser.getInstance().fromBitmask(UInteger.valueOf(2103341L)));
		
		box.setRenderer(new Renderer<Map<String, Boolean>>() {
			@Override
			public String render(Map<String, Boolean> obj) {
				return BitmaskParser.getInstance().toBitmask(obj).toString();
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		displayedNode.setBrowseName(
				new QualifiedName(displayedNode.getBrowseName().getNamespaceIndex(), txtBrowseName.getText()));
		displayedNode.setDescription(
				new LocalizedText(displayedNode.getDescription().getLocale(), txtDescription.getText()));
		displayedNode.setDisplayName(
				new LocalizedText(displayedNode.getDisplayName().getLocale(), txtDisplayName.getText()));
		displayedNode.setNodeClass(cmbNodeClass.getSelected());

		super.okPressed();
	}
}
