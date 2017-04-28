package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.shared.util.EventBus;
import me.steffenjacobs.opcuadisplay.views.attribute.events.AttributeModifiedEvent;

public class NodeGenerator {

	private static NodeGenerator instance;

	private NodeGenerator() {
		// singleton
	}

	public NodeGenerator getInstance() {
		if (instance == null) {
			instance = new NodeGenerator();
		}
		return instance;
	}

	public static void createAndInsert(int nameSpaceIndex, String name, int nodeId, CachedObjectTypeNode type,
			CachedBaseNode parent) {
		CachedObjectNode con = create(nameSpaceIndex, name, nodeId, type);
		parent.addChild(con);
		con.setParent(parent);
		parent.getReferences().add(new CachedReference("Organizes", con.getBrowseName(), type.getBrowseName().getName(),
				type.getNodeId()));

		// rerender tree viewer
		EventBus.getInstance().fireEvent(new AttributeModifiedEvent());
	}

	private static CachedObjectNode create(int nameSpaceIndex, String text, int nodeId, CachedObjectTypeNode type) {
		// TODO: add children recursively
		return CachedObjectNode.create(nameSpaceIndex, text, nodeId, type);
	}

}
