package me.steffenjacobs.opcuadisplay.ui.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ImportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "importWizardCancel";

	public ImportWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
