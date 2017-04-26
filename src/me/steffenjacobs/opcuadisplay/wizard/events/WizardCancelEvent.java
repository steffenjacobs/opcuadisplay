package me.steffenjacobs.opcuadisplay.wizard.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class WizardCancelEvent extends Event {

	public static String IDENTIFIER = "wizardCancel";

	public WizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
