package me.steffenjacobs.opcuadisplay.ui.views.explorer.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;

/**
 * is called when the root had been updated
 * 
 * @author Steffen Jacobs
 */
public class RootUpdatedEvent extends Event {

	public static String IDENTIFIER = "rootUpdatedEvent";

	private final CachedBaseNode node;

	public RootUpdatedEvent(CachedBaseNode selectedNode) {
		super(IDENTIFIER, EventArgs.NONE);
		this.node = selectedNode;
	}

	public CachedBaseNode getNode() {
		return node;
	}
}
