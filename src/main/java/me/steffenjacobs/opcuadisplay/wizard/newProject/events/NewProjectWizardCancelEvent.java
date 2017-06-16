package me.steffenjacobs.opcuadisplay.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class NewProjectWizardCancelEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardCancel";

	public NewProjectWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
