package me.steffenjacobs.opcuadisplay.ui.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;

/** @author Steffen Jacobs */
public class ExportWizardFinishEvent extends Event {

	public static String IDENTIFIER = "exportWizardFinish";

	private final String url, namespace;

	private final boolean baseDataTypesImplicit, freeOpcUaModelerCompatibility;

	public ExportWizardFinishEvent(String url, boolean baseDataTypesImplicit, boolean freeOpcUaModelerCompatibility, String namespace) {
		super(IDENTIFIER, EventArgs.NONE);
		this.url = url;
		this.baseDataTypesImplicit = baseDataTypesImplicit;
		this.freeOpcUaModelerCompatibility = freeOpcUaModelerCompatibility;
		this.namespace = namespace;
	}

	public boolean isBaseDataTypesImplicit() {
		return baseDataTypesImplicit;
	}

	public boolean isFreeOpcUaModelerCompatibility() {
		return freeOpcUaModelerCompatibility;
	}

	public String getUrl() {
		return url;
	}
	
	public String getNamespace() {
		return namespace;
	}
}
