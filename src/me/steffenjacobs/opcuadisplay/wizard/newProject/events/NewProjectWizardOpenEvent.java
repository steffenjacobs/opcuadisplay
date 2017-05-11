package me.steffenjacobs.opcuadisplay.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class NewProjectWizardOpenEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardOpen";

	public NewProjectWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
