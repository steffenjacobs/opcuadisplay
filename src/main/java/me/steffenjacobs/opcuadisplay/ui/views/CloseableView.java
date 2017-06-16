package me.steffenjacobs.opcuadisplay.ui.views;

import org.eclipse.ui.part.ViewPart;

import me.steffenjacobs.opcuadisplay.management.event.eventbus.EventBus;

/** automatically unregisters listeners when view got closed 
 * 
 * @author Steffen Jacobs*/
public abstract class CloseableView extends ViewPart {

	@Override
	public void dispose() {
		EventBus.getInstance().unregisterAllListeners(this);
		super.dispose();
	}

	public abstract String getIdentifier();
}
