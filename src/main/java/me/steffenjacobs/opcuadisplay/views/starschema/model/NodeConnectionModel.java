package me.steffenjacobs.opcuadisplay.views.starschema.model;

public class NodeConnectionModel {

	private NodeModel source;
	private NodeModel target;

	public void setSource(NodeModel s) {
		source = s;
	}

	public void setTarget(NodeModel t) {
		target = t;
	}

	public NodeModel getSource() {
		return source;
	}

	public NodeModel getTarget() {
		return target;
	}
}
