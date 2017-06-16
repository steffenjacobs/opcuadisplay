package me.steffenjacobs.opcuadisplay.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ExportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "exportWizardCancel";

	public ExportWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
