package me.steffenjacobs.opcuadisplay.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class ImportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "importWizardCancel";

	public ImportWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
