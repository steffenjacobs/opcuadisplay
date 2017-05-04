package me.steffenjacobs.opcuadisplay.views.explorer.dialogs;

import java.util.HashMap;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.views.explorer.NodeClassLabelProvider;
import me.steffenjacobs.opcuadisplay.views.explorer.SimpleOpcUaTreeProvider;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory.DialogListener;

public class MethodAddDialog extends SimpleAddDialog {

	private final HashMap<String, MethodArgument> inputArgs = new HashMap<>(), outputArgs = new HashMap<>();

	public MethodAddDialog(Shell parentShell, String title, final DialogListener listener) {
		super(parentShell, title, null, null);
		final DialogListener wrappedListener = new DialogListener() {
			@Override
			public void onOkSimple(int namespace, String name, int nodeId, CachedBaseNode type) {
				listener.onOkMethod(namespace, name, nodeId,
						inputArgs.values().toArray(new MethodArgument[inputArgs.size()]),
						outputArgs.values().toArray(new MethodArgument[outputArgs.size()]));
			}
		};
		super.setDialogListener(wrappedListener);

	}

	public HashMap<String, MethodArgument> getInputArgs() {
		return inputArgs;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		createHorizontalSeperator(area);

		Composite bottom = new Composite(area, SWT.NONE);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(3, false);
		bottom.setLayout(layout);

		createArgumentsPanel(bottom, "Input Arguments", inputArgs);
		createVerticalSeperator(bottom);
		createArgumentsPanel(bottom, "Output Arguments", outputArgs);

		return area;
	}

	private void createVerticalSeperator(Composite parent) {
		Label separator = new Label(parent, SWT.VERTICAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_VERTICAL));
	}

	private void createHorizontalSeperator(Composite parent) {
		Label separator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createArgumentsPanel(Composite parent, String caption, final HashMap<String, MethodArgument> map) {
		Composite comp = new Composite(parent, SWT.NONE);
		final Label lbl = new Label(comp, SWT.NONE | SWT.CENTER);
		lbl.setText(caption);
		lbl.setBounds(0, 0, 220, 19);

		FontData fontData = lbl.getFont().getFontData()[0];
		Font font = new Font(Activator.getDefault().getWorkbench().getDisplay(),
				new FontData(fontData.getName(), fontData.getHeight(), SWT.BOLD));
		lbl.setFont(font);

		final List list = new List(comp, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		list.setBounds(0, 20, 220, 100);

		final TreeViewer viewer = createTreeViewer(comp);

		final Text text = new Text(comp, SWT.BORDER);
		text.setBounds(0, 130, 160, 25);

		list.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				text.setText(list.getItem(list.getSelectionIndex()));
			}
		});

		final Button btnAdd = new Button(comp, SWT.PUSH);
		btnAdd.setText("+");
		btnAdd.setBounds(165, 130, 25, 25);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				if ("".equals(text.getText())) {
					MethodAddDialog.this.setMessage("The name of an argument can not be empty.",
							IMessageProvider.ERROR);
					return;
				}

				CachedBaseNode node = null;
				try {
					node = ((CachedBaseNode) ((IStructuredSelection) viewer.getSelection()).getFirstElement());

				} catch (ClassCastException e) {
					// node would be null -> catched below
				}

				if (node == null) {
					MethodAddDialog.this.setMessage("The type for the argument is not specified",
							IMessageProvider.ERROR);
					return;
				}

				MethodArgument arg = new MethodArgument(text.getText(), node);

				if (map.containsKey(arg.toString())) {
					MethodAddDialog.this.setMessage("The name '" + text.getText() + "' already exists.",
							IMessageProvider.ERROR);
					return;
				}

				map.put(arg.toString(), arg);
				list.add(arg.toString());
			}
		});

		final Button btnRemove = new Button(comp, SWT.PUSH);
		btnRemove.setText("-");
		btnRemove.setBounds(195, 130, 25, 25);
		btnRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				int index = list.getSelectionIndex();
				MethodArgument arg = map.get(text.getText());
				if (arg == null) {
					return;
				}
				map.remove(arg.toString());
				list.remove(arg.toString());

				// update selection
				if (list.getItemCount() > 0) {
					list.setSelection(index >= list.getItemCount() ? list.getItemCount() - 1 : index);
					text.setText(list.getItem(list.getSelectionIndex()));
				}
			}
		});

	}

	private TreeViewer createTreeViewer(Composite container) {

		TreeViewer viewer = new TreeViewer(container, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		viewer.setContentProvider(new SimpleOpcUaTreeProvider());
		CachedBaseNode cbn = CachedBaseNode.createEmptyDummy();

		CachedBaseNode typeNode = NodeNavigator.getInstance().navigateByName("Types/DataTypes/BaseDataType");
		cbn.addChild(typeNode);
		typeNode.setParent(cbn);

		viewer.setInput(cbn);
		viewer.setLabelProvider(new NodeClassLabelProvider());
		viewer.getControl().setBounds(0, 170, 220, 150);

		viewer.setExpandedElements(cbn.getChildren());
		viewer.setExpandedState(cbn, true);
		return viewer;
	}
}
