package me.steffenjacobs.opcuadisplay.wizard.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class WizardFinishEvent extends Event {

	public static String IDENTIFIER = "wizardFinish";

	private final String url;
	private final boolean server;

	public WizardFinishEvent(String url, boolean server) {
		super(IDENTIFIER, EventArgs.NONE);
		this.url = url;
		this.server = server;
	}

	public String getUrl() {
		return url;
	}

	public boolean isServer() {
		return server;
	}
}
