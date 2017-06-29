package me.steffenjacobs.opcuadisplay.ui.views.starschema.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.model.StarSchemaSettings;

public class StarschemaSettingsChangedEvent extends Event {

	public static final String IDENTIFIER = "starschema.settings.changed";
	private final StarSchemaSettings settings;

	public StarschemaSettingsChangedEvent(StarSchemaSettings settings) {
		super(IDENTIFIER, EventArgs.NONE);
		this.settings = settings;
	}

	public StarSchemaSettings getSettings() {
		return settings;
	}
}
