package me.steffenjacobs.opcuadisplay.ui.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;

/** @author Steffen Jacobs */
public class ImportWizardCancelEvent extends Event {

	public static String IDENTIFIER = "importWizardCancel";

	private final boolean mergeWizard;

	public ImportWizardCancelEvent(boolean mergeWizard) {
		super(IDENTIFIER, EventArgs.NONE);
		this.mergeWizard = mergeWizard;
	}

	public boolean isMergeWizard() {
		return mergeWizard;
	}
}
