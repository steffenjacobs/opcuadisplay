package me.steffenjacobs.opcuadisplay.views.starschema.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.views.starschema.ColorSet;

public class NodeModel {

	public static NodeModel EMPTY_NODE = new NodeModel(CachedBaseNode.createEmptyDummy(), ColorSet.WHITE.getColor(), true);

	private final List<NodeConnectionModel> sourceConnections = new ArrayList<NodeConnectionModel>();
	private final List<NodeConnectionModel> targetConnections = new ArrayList<NodeConnectionModel>();

	private final Color color;
	private final CachedBaseNode node;
	private final boolean dummy;

	public NodeModel(CachedBaseNode node) {
		this(node, ColorSet.NODE.getColor(), false);
	}

	public NodeModel(CachedBaseNode node, Color color, boolean dummy) {
		this.node = node;
		this.color = color;
		this.dummy = dummy;
	}

	public boolean isDummy() {
		return dummy;
	}

	public String getLabel() {
		return node.getDisplayName().getText();
	}

	public List<NodeConnectionModel> getSourceConnections() {
		return sourceConnections;
	}

	public List<NodeConnectionModel> getTargetConnections() {
		return targetConnections;
	}

	public void addSourceConnection(NodeConnectionModel iConnection) {
		sourceConnections.add(iConnection);
	}

	public void addTargetConnection(NodeConnectionModel iConnection) {
		targetConnections.add(iConnection);
	}

	public Color getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NodeModel [label=").append(node.getDisplayName().getText()).append("]");
		return builder.toString();
	}

	public CachedBaseNode getNode() {
		return node;
	}	
}