package me.steffenjacobs.opcuadisplay.ui.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class NewProjectWizardOpenEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardOpen";

	public NewProjectWizardOpenEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
