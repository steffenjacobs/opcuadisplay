package me.steffenjacobs.opcuadisplay.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ImportWizardOpenEvent extends Event {

	public static String IDENTIFIER = "importWizardOpen";

	public ImportWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
