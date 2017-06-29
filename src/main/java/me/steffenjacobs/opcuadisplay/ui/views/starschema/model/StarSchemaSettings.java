package me.steffenjacobs.opcuadisplay.ui.views.starschema.model;

public class StarSchemaSettings {
	final int fontSize, boxSize;
	final boolean wordWrap;
	
	
	public StarSchemaSettings(int fontSize, int boxSize, boolean wordWrap) {
		super();
		this.fontSize = fontSize;
		this.boxSize = boxSize;
		this.wordWrap = wordWrap;
	}
	public int getFontSize() {
		return fontSize;
	}
	public int getBoxSize() {
		return boxSize;
	}
	public boolean isWordWrap() {
		return wordWrap;
	}
}
