package me.steffenjacobs.opcuadisplay.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ExportWizardOpenEvent extends Event {

	public static String IDENTIFIER = "exportWizardOpen";

	public ExportWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
