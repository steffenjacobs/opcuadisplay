package me.steffenjacobs.opcuadisplay.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class ExportWizardOpenEvent extends Event {

	public static String IDENTIFIER = "exportWizardOpen";

	public ExportWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
