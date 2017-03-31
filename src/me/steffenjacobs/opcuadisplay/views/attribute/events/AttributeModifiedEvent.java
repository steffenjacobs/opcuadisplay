package me.steffenjacobs.opcuadisplay.views.attribute.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class AttributeModifiedEvent extends Event {

	public static String IDENTIFIER = "attributeChangedEvent";

	public AttributeModifiedEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
