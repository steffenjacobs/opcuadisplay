package me.steffenjacobs.opcuadisplay.ui.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

import me.steffenjacobs.opcuadisplay.ui.views.attribute.AttributeEditorView;
import me.steffenjacobs.opcuadisplay.ui.views.explorer.OpcUaExplorerView;
import me.steffenjacobs.opcuadisplay.ui.views.reference.ReferenceView;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.StarSchemaView;

/**
 * @author Steffen Jacobs
 */
public class OpcUaPerspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout factory) {

		// add left view
		IFolderLayout topLeft = factory.createFolder("left", IPageLayout.LEFT, 0.25f, factory.getEditorArea());
		topLeft.addView(OpcUaExplorerView.ID);
		topLeft.addView("org.eclipse.jdt.junit.ResultView");

		// add bottom view
		IFolderLayout bottom = factory.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, factory.getEditorArea());
		bottom.addView(AttributeEditorView.ID);
		bottom.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottom.addView("org.eclipse.team.ui.GenericHistoryView");
		bottom.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);

		// add right view
		IFolderLayout right = factory.createFolder("right", IPageLayout.RIGHT, 0.5f, factory.getEditorArea());
		right.addView(ReferenceView.ID);
		
		// add center view
		IFolderLayout center = factory.createFolder("top", IPageLayout.TOP, 1f, factory.getEditorArea());
		center.addView(StarSchemaView.ID);
	}
}
