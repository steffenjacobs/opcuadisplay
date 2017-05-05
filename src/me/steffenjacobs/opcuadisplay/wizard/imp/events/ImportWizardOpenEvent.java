package me.steffenjacobs.opcuadisplay.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class ImportWizardOpenEvent extends Event {

	public static String IDENTIFIER = "importWizardOpen";

	public ImportWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
