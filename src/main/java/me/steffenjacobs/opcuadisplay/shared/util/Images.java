package me.steffenjacobs.opcuadisplay.shared.util;

import me.steffenjacobs.opcuadisplay.Activator;
/** @author Steffen Jacobs */
public enum Images {
	IMG_COLLAPSE_ALL("src/main/resources/icons/collapseall.png", "collapseall"),
	IMG_EXPAND_ALL("src/main/resources/icons/expandall.png", "expandall"),
	IMG_IMPORT("src/main/resources/icons/import.png", "import"),
	IMG_EXPORT("src/main/resources/icons/export.png", "export"),
	OPCUA("src/main/resources/icons/opcua.png", "opocua");

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
		TYPE("src/main/resources/icons/view/type.png", "type"),
		PROPERTY("src/main/resources/icons/view/file.png", "property"),
		VARIABLE("src/main/resources/icons/view/file_yellow.png", "variable"),
		FOLDER("src/main/resources/icons/view/folder.png", "folder"),
		METHOD("src/main/resources/icons/view/method.png", "method"),
		OBJECT("src/main/resources/icons/view/object.png", "object"),
		OBJECT_TYPE("src/main/resources/icons/view/objectType.png", "objectType"),
		REFERENCE_TYPE("src/main/resources/icons/view/referenceType.png", "referenceType"),
		REFERENCE("src/main/resources/icons/view/reference.png", "reference"),
		DATA_TYPE("src/main/resources/icons/view/dataType.png", "dataType"),
		VARIABLE_TYPE("src/main/resources/icons/view/variableType.png", "variableType"),
		REMOVE("src/main/resources/icons/view/delete.png", "remove"),
		UNKNOWN("src/main/resources/icons/view/unknown.png", "unknown");

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
		CHECKED("src/main/resources/icons/view/attribute/checked.gif", "checked"),
		UNCHECKED("src/main/resources/icons/view/attribute/unchecked.gif", "unchecked");

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

	public static enum StarSchemaView {
		UP("src/main/resources/icons/view/starschema/up.gif", "up");

		private final String path, identifier;

		StarSchemaView(String path, String identifier) {
			this.path = path;
			this.identifier = Activator.PLUGIN_ID + ".view.starschema." + identifier;
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
