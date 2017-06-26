package me.steffenjacobs.opcuadisplay.ui.wizard.imp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
/** @author Steffen Jacobs */
public class ImportWizardFinishEvent extends Event {

	public static String IDENTIFIER = "importWizardFinish";

	private final String url;
	private final boolean server, baseDataTypesImplicit, freeOPpcUaModeler;

	public ImportWizardFinishEvent(String url, boolean server, boolean baseDataTypesImplicit, boolean freeOPpcUaModeler) {
		super(IDENTIFIER, EventArgs.NONE);
		this.url = url;
		this.server = server;
		this.baseDataTypesImplicit = baseDataTypesImplicit;
		this.freeOPpcUaModeler = freeOPpcUaModeler;
	}

	public String getUrl() {
		return url;
	}

	public boolean isServer() {
		return server;
	}
	
	public boolean isFreeOpcUaModeler() {
		return freeOPpcUaModeler;
	}

	public boolean isBaseDataTypesImplicit() {
		return baseDataTypesImplicit;
	}
}
