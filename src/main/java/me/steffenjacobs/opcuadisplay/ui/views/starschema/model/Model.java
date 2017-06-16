package me.steffenjacobs.opcuadisplay.ui.views.starschema.model;

import java.util.List;

public class Model {
	private List<NodeModel> nodes;
	
	public Model(List<NodeModel> nodes){
		this.nodes = nodes;
	}

	public List<NodeModel> getNodes() {
		return nodes;
	}
}