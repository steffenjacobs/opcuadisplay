package me.steffenjacobs.opcuadisplay.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class ImportWizardFinishEvent extends Event {

	public static String IDENTIFIER = "importWizardFinish";

	private final String url;
	private final boolean server;

	public ImportWizardFinishEvent(String url, boolean server) {
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
