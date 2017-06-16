package me.steffenjacobs.opcuadisplay.views.explorer.events;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;

/**is called to change the selected node
 * 
 * @author Steffen Jacobs*/
public class ChangeSelectedNodeEvent extends Event {

	public static String IDENTIFIER = "changeSelectedNodeEvent";

	private final CachedBaseNode node;

	private final boolean revealInTree;

	public ChangeSelectedNodeEvent(CachedBaseNode selectedNode, boolean revealInTree) {
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
