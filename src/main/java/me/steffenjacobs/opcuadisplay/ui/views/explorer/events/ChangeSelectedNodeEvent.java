package me.steffenjacobs.opcuadisplay.ui.views.explorer.events;

import me.steffenjacobs.opcuadisplay.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.eventbus.EventBus.EventArgs;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;

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
