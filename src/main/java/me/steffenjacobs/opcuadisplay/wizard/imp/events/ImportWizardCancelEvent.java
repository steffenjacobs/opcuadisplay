package me.steffenjacobs.opcuadisplay.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ImportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "importWizardCancel";

	public ImportWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
