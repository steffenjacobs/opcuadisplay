package me.steffenjacobs.opcuadisplay.wizard.newProject.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class NewProjectWizardFinishEvent extends Event {

	public static String IDENTIFIER = "newProjectWizardFinish";

	private final boolean generateFolders, generateBaseTypes;

	public NewProjectWizardFinishEvent(boolean generateFolders, boolean generateBaseTypes) {
		super(IDENTIFIER, EventArgs.NONE);
		this.generateFolders = generateFolders;
		this.generateBaseTypes = generateBaseTypes;
	}

	public boolean isGenerateFolders() {
		return generateFolders;
	}

	public boolean isGenerateBaseTypes() {
		return generateBaseTypes;
	}
}
