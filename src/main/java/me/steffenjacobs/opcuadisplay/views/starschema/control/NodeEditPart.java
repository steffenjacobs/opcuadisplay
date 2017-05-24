package me.steffenjacobs.opcuadisplay.views.starschema.control;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Color;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.views.explorer.events.ChangeSelectedNodeEvent;
import me.steffenjacobs.opcuadisplay.views.starschema.ColorSet;
import me.steffenjacobs.opcuadisplay.views.starschema.model.NodeModel;

public class NodeEditPart extends AbstractGraphicalEditPart {

	@Override
	protected IFigure createFigure() {

		final NodeModel m = ((NodeModel) getModel());

		final RoundedRectangle rectangle = new RoundedRectangle();
		rectangle.setBackgroundColor(m.getColor());

		if (m.isDummy()) {
			// remove border
			rectangle.setBorder(new LineBorder(ColorSet.WHITE.getColor()));
			rectangle.setCornerDimensions(new Dimension(0, 0));
		} else {
			rectangle.setCornerDimensions(new Dimension(20, 20));

			// add listeners
			addListeners(m, rectangle);
		}
		return rectangle;
	}

	/**
	 * - adding a mouse-click listener to respond to clicking<br>
	 * - adding a mouse-move listener to handle mouse-enter and mouse-exit
	 */
	private void addListeners(final NodeModel m, final IFigure rectangle) {
		rectangle.addMouseListener(new MouseListener() {

			// this is necessary, because the second mouseRelease event from a
			// double click would also be caught here, causing the node that
			// spawned under the parent to be clicked immediately
			boolean pressed = false;

			@Override
			public void mouseReleased(MouseEvent arg0) {
				Color hoverColor = new Color(null, m.getColor().getRed() - 10, m.getColor().getGreen() - 10,
						m.getColor().getBlue() - 10);
				rectangle.setBackgroundColor(hoverColor);
				if (m.getNode() != NodeNavigator.getInstance().getSelectedNode() && pressed) {
					EventBus.getInstance().fireEvent(new ChangeSelectedNodeEvent(m.getNode(), true));
				}
				pressed = false;
			}

			@Override
			public void mousePressed(MouseEvent me) {
				Color darker = new Color(null, m.getColor().getRed() - 30, m.getColor().getGreen() - 30,
						m.getColor().getBlue() - 30);
				rectangle.setBackgroundColor(darker);
				pressed = true;
			}

			@Override
			public void mouseDoubleClicked(MouseEvent me) {
				if (m.getNode() == NodeNavigator.getInstance().getSelectedNode() && m.getNode().getParent() != null) {
					EventBus.getInstance().fireEvent(new ChangeSelectedNodeEvent(m.getNode().getParent(), true));
				}
			}
		});

		rectangle.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseExited(MouseEvent me) {
				rectangle.setBackgroundColor(m.getColor());
			}

			@Override
			public void mouseEntered(MouseEvent me) {
				Color hoverColor = new Color(null, m.getColor().getRed() - 10, m.getColor().getGreen() - 10,
						m.getColor().getBlue() - 10);
				rectangle.setBackgroundColor(hoverColor);
			}

			@Override
			public void mouseDragged(MouseEvent me) {
			}

			@Override
			public void mouseMoved(MouseEvent me) {
			}

			@Override
			public void mouseHover(MouseEvent me) {
			}
		});
	}

	@Override
	protected void createEditPolicies() {
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void refreshVisuals() {
		NodeModel node = (NodeModel) getModel();

		// draw rectangle with text
		Rectangle bounds = new Rectangle(150, 150, 150, 150);
		getFigure().setBounds(bounds);
		Label label = new Label(node.getLabel());
		label.setTextAlignment(PositionConstants.CENTER);
		label.setBounds(bounds.crop(IFigure.NO_INSETS));
		getFigure().add(label);
	}

	@Override
	protected List<?> getModelSourceConnections() {
		List<?> sourceConnections = ((NodeModel) getModel()).getSourceConnections();
		return sourceConnections;
	}

	@Override
	protected List<?> getModelTargetConnections() {
		List<?> targetConnection = ((NodeModel) getModel()).getTargetConnections();
		return targetConnection;
	}

	@Override
	protected ConnectionEditPart createConnection(Object iModel) {
		NodeConnectionEditPart connectPart = (NodeConnectionEditPart) getRoot().getViewer().getEditPartRegistry()
				.get(iModel);
		if (connectPart == null) {
			connectPart = new NodeConnectionEditPart();
			connectPart.setModel(iModel);
		}
		return connectPart;
	}
}
