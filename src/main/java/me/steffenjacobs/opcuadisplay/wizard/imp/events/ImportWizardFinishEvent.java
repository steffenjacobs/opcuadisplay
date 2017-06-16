package me.steffenjacobs.opcuadisplay.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ImportWizardFinishEvent extends Event {

	public static String IDENTIFIER = "importWizardFinish";

	private final String url;
	private final boolean server, baseDataTypesImplicit;

	public ImportWizardFinishEvent(String url, boolean server, boolean baseDataTypesImplicit) {
		super(IDENTIFIER, EventArgs.NONE);
		this.url = url;
		this.server = server;
		this.baseDataTypesImplicit = baseDataTypesImplicit;
	}

	public String getUrl() {
		return url;
	}

	public boolean isServer() {
		return server;
	}

	public boolean isBaseDataTypesImplicit() {
		return baseDataTypesImplicit;
	}
}
