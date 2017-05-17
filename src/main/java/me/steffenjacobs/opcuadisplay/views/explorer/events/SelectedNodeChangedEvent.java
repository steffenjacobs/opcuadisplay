package me.steffenjacobs.opcuadisplay.views.explorer.events;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

/** is called, after the selected node has changed */
public class SelectedNodeChangedEvent extends Event {

	public static String IDENTIFIER = "selectedNodeChangedEvent";

	private final CachedBaseNode node;

	public SelectedNodeChangedEvent(CachedBaseNode selectedNode) {
		super(IDENTIFIER, EventArgs.NONE);
		this.node = selectedNode;
	}

	public CachedBaseNode getNode() {
		return node;
	}
}
