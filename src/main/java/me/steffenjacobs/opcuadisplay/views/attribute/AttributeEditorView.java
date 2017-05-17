package me.steffenjacobs.opcuadisplay.views.attribute;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.views.CloseableView;
import me.steffenjacobs.opcuadisplay.views.attribute.domain.NodeEntryFactory;
import me.steffenjacobs.opcuadisplay.views.attribute.domain.NodeEntryFactory.NodeEntry;
import me.steffenjacobs.opcuadisplay.views.explorer.events.SelectedNodeChangedEvent;
/** @author Steffen Jacobs */
public class AttributeEditorView extends CloseableView {

	public static final String ID = "me.steffenjacobs.opcuadisplay.views.attribute.AttributeEditorView";

	private TableViewer viewer;

	private AttributeEditorViewTableEditor tableEditor;

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		parent.setLayout(layout);
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search: ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		createViewer(parent);
		registerListeners();
	}

	private void registerListeners() {
		EventBus.getInstance().addListener(this, SelectedNodeChangedEvent.IDENTIFIER,
				new EventListener<SelectedNodeChangedEvent>() {

					@Override
					public void onAction(SelectedNodeChangedEvent event) {
						tableEditor.clearTableEditor();
						viewer.setInput(NodeEntryFactory.fromNode(event.getNode()));
						viewer.refresh();
					}
				});
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new ArrayContentProvider());
		// make the selection available to other views
		getSite().setSelectionProvider(viewer);
		// set the sorter for the table

		// define layout for the viewer
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		viewer.getControl().setLayoutData(gridData);

		tableEditor = new AttributeEditorViewTableEditor(table, this.getViewer());
	}

	public TableViewer getViewer() {
		return viewer;
	}

	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles =
			{ "Attribute Name", "Value", "Data Type" };
		int[] bounds =
			{ 150, 200, 100 };

		// attribute name
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NodeEntry<?> n = (NodeEntry<?>) element;
				return n.getText();
			}
		});

		// attribute value
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NodeEntry<?> n = (NodeEntry<?>) element;

				return AttributeValueParser.asString(n.getValue());
			}
		});

		// data type
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				NodeEntry<?> n = (NodeEntry<?>) element;
				return n.getTypeName();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
