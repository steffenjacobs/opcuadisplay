package me.steffenjacobs.opcuadisplay.views.explorer.events;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus.EventArgs;

public class SelectedNodeChangedEvent extends Event {

	public static String IDENTIFIER = "selectedNodeChangedEvent";

	private final CachedBaseNode node;

	private final boolean revealInTree;

	public SelectedNodeChangedEvent(CachedBaseNode selectedNode, boolean revealInTree) {
		super(IDENTIFIER, EventArgs.NONE);
		this.node = selectedNode;
		this.revealInTree = revealInTree;
	}

	public CachedBaseNode getNode() {
		return node;
	}

	public boolean isRevealInTree() {
		return revealInTree;
	}
}
