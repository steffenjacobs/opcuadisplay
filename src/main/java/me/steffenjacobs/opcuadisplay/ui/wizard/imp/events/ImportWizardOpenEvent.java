package me.steffenjacobs.opcuadisplay.ui.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ImportWizardOpenEvent extends Event {

	public static String IDENTIFIER = "importWizardOpen";

	public ImportWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
