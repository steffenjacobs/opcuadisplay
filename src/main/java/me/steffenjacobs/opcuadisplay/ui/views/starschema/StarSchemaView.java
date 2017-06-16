package me.steffenjacobs.opcuadisplay.ui.views.starschema;

import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;
import me.steffenjacobs.opcuadisplay.ui.Images;
import me.steffenjacobs.opcuadisplay.ui.views.CloseableView;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.events.ChangeSelectedNodeEvent;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.events.SelectedNodeChangedEvent;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.control.GraphicalPartFactory;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.Model;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.ModelCreator;

public class StarSchemaView extends CloseableView {
	public static final String ID = "me.steffenjacobs.opcuadisplay.ui.views.starschema.StarSchemaView";

	private ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {

		// init viewer
		viewer.createControl(parent);
		viewer.setRootEditPart(new FreeformGraphicalRootEditPart());
		viewer.getControl().setBackground(ColorSet.WHITE.getColor());

		addListeners();
		initToolbar();
	}

	private void initToolbar() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {

		// collapse all action
		Action jumpToParentNode = new Action() {
			public void run() {
				if (NodeNavigator.getInstance().getSelectedNode() != null
						&& NodeNavigator.getInstance().getSelectedNode().getParent() != null) {
					EventBus.getInstance().fireEvent(new ChangeSelectedNodeEvent(
							NodeNavigator.getInstance().getSelectedNode().getParent(), true));
				}
			}
		};
		jumpToParentNode.setText("Jump to parent node");
		jumpToParentNode.setToolTipText("Jump to parent node");
		jumpToParentNode.setImageDescriptor(Activator.getImageDescriptor(Images.StarSchemaView.UP.getIdentifier()));

		manager.add(jumpToParentNode);
	}

	private void addListeners() {
		EventBus.getInstance().addListener(this, SelectedNodeChangedEvent.IDENTIFIER,
				new EventListener<SelectedNodeChangedEvent>() {

					@Override
					public void onAction(SelectedNodeChangedEvent event) {
						if (event.getNode() != null && !event.getNode().isDummy()) {
							Model model = ModelCreator.getInstance().createModel(event.getNode());

							viewer.setEditPartFactory(
									new GraphicalPartFactory(new SpiralLogic(model.getNodes().size())));

							viewer.setContents(model);
							viewer.flush();
						}
					}
				});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public String getIdentifier() {
		return ID;
	}
}
