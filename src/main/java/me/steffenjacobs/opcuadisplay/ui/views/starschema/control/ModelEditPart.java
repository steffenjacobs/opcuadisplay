package me.steffenjacobs.opcuadisplay.ui.views.starschema.control;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import me.steffenjacobs.opcuadisplay.ui.views.starschema.SpiralLogic;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.Model;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.NodeModel;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.StarSchemaSettings;

public class ModelEditPart extends AbstractGraphicalEditPart {

	private final SpiralLogic logic;
	private final StarSchemaSettings settings;
	private Figure f;

	public ModelEditPart(SpiralLogic logic, StarSchemaSettings settings) {
		this.logic = logic;
		this.settings = settings;
	}

	@Override
	protected IFigure createFigure() {
		f = new FreeformLayer();
		f.setLayoutManager(new FreeformLayout());

		f.setBorder(new MarginBorder(1));
		// Create a layout for the graphical screen
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = logic.getLength();
		gridLayout.horizontalSpacing = settings.getBoxSize()/3;
		gridLayout.verticalSpacing = settings.getBoxSize()/3;
		gridLayout.marginHeight = 25;
		gridLayout.marginWidth = 25;
		f.setLayoutManager(gridLayout);
		f.setOpaque(true);

		return f;
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	protected List<NodeModel> getModelChildren() {
		List<NodeModel> children = ((Model) getModel()).getNodes();
		return children;
	}
}
