package me.steffenjacobs.opcuadisplay.ui.views.attribute.events;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.EventArgs;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
/** @author Steffen Jacobs */
public class AttributeModifiedEvent extends Event {

	public static String IDENTIFIER = "attributeChangedEvent";

	private final CachedBaseNode changedNode;

	public AttributeModifiedEvent(CachedBaseNode changedNode) {
		super(IDENTIFIER, EventArgs.NONE);
		this.changedNode = changedNode;
	}

	public CachedBaseNode getChangedNode() {
		return changedNode;
	}
}
