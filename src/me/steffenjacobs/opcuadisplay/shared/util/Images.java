package me.steffenjacobs.opcuadisplay.shared.util;

import me.steffenjacobs.opcuadisplay.Activator;

public enum Images {
	IMG_COLLAPSE_ALL("icons/collapseall.png", "collapseall"), IMG_EXPAND_ALL("icons/expandall.png", "expandall");

	private final String path, identifier;

	Images(String path, String identifier) {
		this.path = path;
		this.identifier = Activator.PLUGIN_ID + "." + identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return identifier;
	}

	public static enum ExplorerView {
		TYPE("icons/view/type.png", "variabletype"), FILE("icons/view/file.png", "file"), FILE_YELLOW(
				"icons/view/file_yellow.png", "fileyellow"), FOLDER("icons/view/folder.png",
						"folder"), METHOD("icons/view/method.png", "method"), OBJECT("icons/view/object.png",
								"object"), UNKNOWN("icons/view/unknown.png", "unknown");

		private final String path, identifier;

		ExplorerView(String path, String identifier) {
			this.path = path;
			this.identifier = Activator.PLUGIN_ID + ".view.explorer." + identifier;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getPath() {
			return path;
		}

		@Override
		public String toString() {
			return identifier;
		}
	}

	public static enum AttributeEditorView {
		CHECKED("icons/view/attribute/checked.gif", "checked"), UNCHECKED("icons/view/attribute/unchecked.gif",
				"unchecked");

		private final String path, identifier;

		AttributeEditorView(String path, String identifier) {
			this.path = path;
			this.identifier = Activator.PLUGIN_ID + ".view.attribute." + identifier;
		}

		public String getIdentifier() {
			return identifier;
		}

		public String getPath() {
			return path;
		}

		@Override
		public String toString() {
			return identifier;
		}
	}
}
