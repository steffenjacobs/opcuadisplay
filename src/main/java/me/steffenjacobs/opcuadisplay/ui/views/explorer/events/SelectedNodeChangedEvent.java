package me.steffenjacobs.opcuadisplay.ui.views.explorer.events;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.EventArgs;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;

/** is called, after the selected node has changed 
 * @author Steffen Jacobs*/
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
