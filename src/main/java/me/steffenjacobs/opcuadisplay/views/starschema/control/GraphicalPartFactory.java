package me.steffenjacobs.opcuadisplay.views.starschema.control;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import me.steffenjacobs.opcuadisplay.views.starschema.SpiralLogic;
import me.steffenjacobs.opcuadisplay.views.starschema.model.Model;
import me.steffenjacobs.opcuadisplay.views.starschema.model.NodeModel;

public class GraphicalPartFactory implements EditPartFactory {

	private SpiralLogic logic;

	public GraphicalPartFactory(SpiralLogic logic) {
		this.logic = logic;
	}

	public EditPart createEditPart(EditPart iContext, Object iModel) {

		EditPart editPart = null;
		if (iModel instanceof Model) {
			editPart = new ModelEditPart(logic);
		} else if (iModel instanceof NodeModel) {
			editPart = new NodeEditPart();
		}

		if (editPart != null) {
			editPart.setModel(iModel);
		}
		return editPart;
	}
}
