package me.steffenjacobs.opcuadisplay.views.starschema;

import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.views.CloseableView;
import me.steffenjacobs.opcuadisplay.views.explorer.events.SelectedNodeChangedEvent;
import me.steffenjacobs.opcuadisplay.views.starschema.control.GraphicalPartFactory;
import me.steffenjacobs.opcuadisplay.views.starschema.model.Model;
import me.steffenjacobs.opcuadisplay.views.starschema.model.ModelCreator;

public class StarSchemaView extends CloseableView {
	public static final String ID = "me.steffenjacobs.opcuadisplay.views.starschema.StarSchemaView";

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
