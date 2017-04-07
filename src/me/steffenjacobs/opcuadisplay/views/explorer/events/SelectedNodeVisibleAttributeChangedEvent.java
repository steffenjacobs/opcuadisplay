package me.steffenjacobs.opcuadisplay.views.explorer.events;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class SelectedNodeVisibleAttributeChangedEvent extends Event {

	public static String IDENTIFIER = "selectedNodeVisibleAttributeChangedEvent";
	
	private final CachedBaseNode node;

	public SelectedNodeVisibleAttributeChangedEvent(CachedBaseNode selectedNode) {
		super(IDENTIFIER, EventArgs.NONE);
		this.node = selectedNode;
	}
	
	public CachedBaseNode getNode() {
		return node;
	}
}
