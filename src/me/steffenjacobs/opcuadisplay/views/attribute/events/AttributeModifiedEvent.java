package me.steffenjacobs.opcuadisplay.views.attribute.events;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

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
