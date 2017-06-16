package me.steffenjacobs.opcuadisplay.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class NewProjectWizardOpenEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardOpen";

	public NewProjectWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
