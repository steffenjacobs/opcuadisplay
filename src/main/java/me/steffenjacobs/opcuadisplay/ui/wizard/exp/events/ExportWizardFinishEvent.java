package me.steffenjacobs.opcuadisplay.ui.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ExportWizardFinishEvent extends Event {

	public static String IDENTIFIER = "exportWizardFinish";

	private final String url;

	public ExportWizardFinishEvent(String url) {
		super(IDENTIFIER, EventArgs.NONE);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
