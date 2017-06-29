package me.steffenjacobs.opcuadisplay.ui.views.starschema;

import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;
import me.steffenjacobs.opcuadisplay.ui.Images;
import me.steffenjacobs.opcuadisplay.ui.views.CloseableView;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.events.ChangeSelectedNodeEvent;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.events.SelectedNodeChangedEvent;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.control.GraphicalPartFactory;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.events.StarschemaSettingsChangedEvent;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.Model;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.ModelCreator;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.StarSchemaSettings;

public class StarSchemaView extends CloseableView {
	public static final String ID = "me.steffenjacobs.opcuadisplay.ui.views.starschema.StarSchemaView";

	private ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();

	private Model currentModel = null;
	private StarSchemaSettings settings = new StarSchemaSettings(11, 150, false);

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

		// jump to parent
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

		// zoom in
		Action zoomIn = new Action() {
			public void run() {
				EventBus.getInstance()
				.fireEvent(new StarschemaSettingsChangedEvent(new StarSchemaSettings(settings.getFontSize() + 1,
						(int) (settings.getBoxSize() * 1.2), false)));
			}
		};
		zoomIn.setText("+");
		zoomIn.setToolTipText("Zoom in");
		zoomIn.setImageDescriptor(Activator.getImageDescriptor(Images.StarSchemaView.ZOOM_IN.getIdentifier()));

		manager.add(zoomIn);

		// zoom out
		Action zoomOut = new Action() {
			public void run() {
				if(settings.getFontSize()>2){
				EventBus.getInstance()
						.fireEvent(new StarschemaSettingsChangedEvent(new StarSchemaSettings(settings.getFontSize() - 1,
								(int) (settings.getBoxSize() * 0.833), false)));
				}
			}
		};
		zoomOut.setText("-");
		zoomOut.setToolTipText("Zoom out");
		zoomOut.setImageDescriptor(Activator.getImageDescriptor(Images.StarSchemaView.ZOOM_OUT.getIdentifier()));

		manager.add(zoomOut);
	}

	private void addListeners() {
		EventBus.getInstance().addListener(this, SelectedNodeChangedEvent.IDENTIFIER,
				new EventListener<SelectedNodeChangedEvent>() {

					@Override
					public void onAction(SelectedNodeChangedEvent event) {
						if (event.getNode() != null && !event.getNode().isDummy()) {
							currentModel = ModelCreator.getInstance().createModel(event.getNode());

							viewer.setEditPartFactory(new GraphicalPartFactory(
									new SpiralLogic(currentModel.getNodes().size()), settings));

							viewer.setContents(currentModel);
							viewer.flush();
						}
					}
				});

		EventBus.getInstance().addListener(this, StarschemaSettingsChangedEvent.IDENTIFIER,
				new EventListener<StarschemaSettingsChangedEvent>() {

					@Override
					public void onAction(StarschemaSettingsChangedEvent event) {
						settings = event.getSettings();
						viewer.setEditPartFactory(
								new GraphicalPartFactory(new SpiralLogic(currentModel.getNodes().size()), settings));

						viewer.setContents(currentModel);
						viewer.flush();
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
