package me.steffenjacobs.opcuadisplay.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class NewProjectWizardCancelEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardCancel";

	public NewProjectWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
