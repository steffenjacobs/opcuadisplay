package me.steffenjacobs.opcuadisplay.wizard.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class WizardOpenEvent extends Event {

	public static String IDENTIFIER = "wizardOpen";

	public WizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
