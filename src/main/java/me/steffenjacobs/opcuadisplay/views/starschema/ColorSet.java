package me.steffenjacobs.opcuadisplay.views.starschema;

import org.eclipse.swt.graphics.Color;

public enum ColorSet {
	NO_BORDER(new Color(null, 255, 255, 255)),
	SELECTED_NODE(new Color(null, 200, 208, 208)),
	NODE(new Color(null, 240, 243, 243)),
	WHITE(new Color(null, 255, 255, 255));

	private final Color color;

	private ColorSet(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

}
