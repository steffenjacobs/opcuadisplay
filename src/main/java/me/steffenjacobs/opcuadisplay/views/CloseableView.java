package me.steffenjacobs.opcuadisplay.views;

import org.eclipse.ui.part.ViewPart;

import me.steffenjacobs.opcuadisplay.shared.util.EventBus;

/** automatically unregisters listeners when view got closed */
public abstract class CloseableView extends ViewPart {

	@Override
	public void dispose() {
		EventBus.getInstance().unregisterAllListeners(this);
		super.dispose();
	}

	public abstract String getIdentifier();
}
