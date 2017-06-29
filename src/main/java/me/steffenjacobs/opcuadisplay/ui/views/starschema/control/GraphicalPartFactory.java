package me.steffenjacobs.opcuadisplay.ui.views.starschema.control;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import me.steffenjacobs.opcuadisplay.ui.views.starschema.SpiralLogic;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.Model;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.NodeModel;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.StarSchemaSettings;

/** create the visualization of the models */
public class GraphicalPartFactory implements EditPartFactory {

	private SpiralLogic logic;

	final StarSchemaSettings settings;

	public GraphicalPartFactory(SpiralLogic logic, StarSchemaSettings settings) {
		this.logic = logic;
		this.settings = settings;
	}

	@Override
	public EditPart createEditPart(EditPart iContext, Object iModel) {

		EditPart editPart = null;
		if (iModel instanceof Model) {
			editPart = new ModelEditPart(logic, settings);
		} else if (iModel instanceof NodeModel) {
			editPart = new NodeEditPart(settings);
		}

		if (editPart != null) {
			editPart.setModel(iModel);
		}
		return editPart;
	}
}
