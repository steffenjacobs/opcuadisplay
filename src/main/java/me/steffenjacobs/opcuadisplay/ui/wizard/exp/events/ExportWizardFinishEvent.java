package me.steffenjacobs.opcuadisplay.ui.wizard.exp.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;

/** @author Steffen Jacobs */
public class ExportWizardFinishEvent extends Event {

	public static String IDENTIFIER = "exportWizardFinish";

	private final String url, nameSpaceId;

	private final boolean baseDataTypesImplicit, freeOpcUaModelerCompatibility;

	public ExportWizardFinishEvent(String url, boolean baseDataTypesImplicit, boolean freeOpcUaModelerCompatibility, String nameSpaceId) {
		super(IDENTIFIER, EventArgs.NONE);
		this.url = url;
		this.baseDataTypesImplicit = baseDataTypesImplicit;
		this.freeOpcUaModelerCompatibility = freeOpcUaModelerCompatibility;
		this.nameSpaceId = nameSpaceId;
	}

	public boolean isBaseDataTypesImplicit() {
		return baseDataTypesImplicit;
	}

	public boolean isFreeOpcUaModelerCompatibility() {
		return freeOpcUaModelerCompatibility;
	}
	
	public String getNameSpaceId() {
		return nameSpaceId;
	}

	public String getUrl() {
		return url;
	}
}
