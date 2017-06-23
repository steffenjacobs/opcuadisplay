package me.steffenjacobs.opcuadisplay.ui.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class NewProjectWizardCancelEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardCancel";

	public NewProjectWizardCancelEvent() {
		super(IDENTIFIER, EventArgs.NONE);
	}
}
