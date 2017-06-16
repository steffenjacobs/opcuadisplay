package me.steffenjacobs.opcuadisplay.ui.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ExportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "exportWizardCancel";

	public ExportWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
