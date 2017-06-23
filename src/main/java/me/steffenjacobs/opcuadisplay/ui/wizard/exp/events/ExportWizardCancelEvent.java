package me.steffenjacobs.opcuadisplay.ui.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ExportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "exportWizardCancel";

	public ExportWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
