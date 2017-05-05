package me.steffenjacobs.opcuadisplay.views.reference;

import java.util.ArrayList;

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
import org.eclipse.ui.part.ViewPart;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.views.attribute.events.AttributeModifiedEvent;
import me.steffenjacobs.opcuadisplay.views.explorer.events.SelectedNodeChangedEvent;

/**
 * A view, which shows the references associated with the current (parent) node
 * relative to its children. These references can be hierarchical or
 * non-hierarchical. <br>
 * The view is update, when a AttributeModifiedEvent is caught (a child may have
 * been added) or the selection in the explorer view changed
 */
public class ReferenceView extends ViewPart {

	public static final String ID = "me.steffenjacobs.opcuadisplay.views.reference.ReferenceView";

	private TableViewer viewer;

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

		// listener for selection change in explorer view
		EventBus.getInstance().addListener(SelectedNodeChangedEvent.IDENTIFIER,
				new EventListener<SelectedNodeChangedEvent>() {

					@Override
					public void onAction(SelectedNodeChangedEvent event) {
						viewer.setInput(event.getNode() == null ? new ArrayList<CachedReference>()
								: event.getNode().getReferences());
						viewer.refresh();
					}
				});

		// listener for attribute modification -> utilized to update, when a
		// subnode is added or removed
		EventBus.getInstance().addListener(AttributeModifiedEvent.IDENTIFIER,
				new EventListener<AttributeModifiedEvent>() {
					@Override
					public void onAction(AttributeModifiedEvent event) {
						viewer.setInput(event.getChangedNode() == null ? new ArrayList<CachedReference>()
								: event.getChangedNode().getReferences());
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

	}

	public TableViewer getViewer() {
		return viewer;
	}

	// create the columns for the table
	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "ReferenceType", "Browse Name", "TypeDefinition", "NodeId" };
		int[] bounds = { 150, 120, 100, 80 };

		// reference type
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedReference n = (CachedReference) element;
				return n.getReferenceType();
			}
		});

		// browse name
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedReference n = (CachedReference) element;
				return ReferenceValueParser.asString(n.getBrowseName());
			}
		});

		// type definition
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedReference n = (CachedReference) element;
				return n.getTypeDefinition();
			}
		});

		// node id
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				CachedReference n = (CachedReference) element;

				return ReferenceValueParser.asString(n.getRefNodeId());
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

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
