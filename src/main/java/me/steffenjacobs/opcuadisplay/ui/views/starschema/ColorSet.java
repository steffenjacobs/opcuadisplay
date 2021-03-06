package me.steffenjacobs.opcuadisplay.ui.views.starschema;

import org.eclipse.swt.graphics.Color;

/** set of colors for the node visualization */
public enum ColorSet {
	SELECTED_NODE(new Color(null, 208, 223, 239)),
	NODE(new Color(null, 230, 234, 248)),
	WHITE(new Color(null, 255, 255, 255));

	private final Color color;

	private ColorSet(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
