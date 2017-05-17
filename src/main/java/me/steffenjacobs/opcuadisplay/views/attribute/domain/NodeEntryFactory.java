package me.steffenjacobs.opcuadisplay.views.attribute.domain;

import java.util.ArrayList;

import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReferenceTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedViewNode;
/** @author Steffen Jacobs */
public class NodeEntryFactory {

	public static Iterable<NodeEntry<?>> fromNode(CachedBaseNode node) {
		if (node instanceof CachedDataTypeNode) {
			return fromNode((CachedDataTypeNode) node);
		} else if (node instanceof CachedMethodNode) {
			return fromNode((CachedMethodNode) node);
		} else if (node instanceof CachedObjectNode) {
			return fromNode((CachedObjectNode) node);
		} else if (node instanceof CachedObjectTypeNode) {
			return fromNode((CachedObjectTypeNode) node);
		} else if (node instanceof CachedReferenceTypeNode) {
			return fromNode((CachedReferenceTypeNode) node);
		} else if (node instanceof CachedVariableNode) {
			return fromNode((CachedVariableNode) node);
		} else if (node instanceof CachedVariableTypeNode) {
			return fromNode((CachedVariableTypeNode) node);
		} else if (node instanceof CachedViewNode) {
			return fromNode((CachedViewNode) node);
		} else {
			return fromBaseNode(node);
		}
	}

	private static Iterable<NodeEntry<?>> fromBaseNode(CachedBaseNode node) {
		ArrayList<NodeEntry<?>> list = new ArrayList<>();
		if (node == null || node.isDummy()) {
			return list;
		}

		list.add(new NodeEntry<QualifiedName>("BrowseName", node.getBrowseName(), node));
		list.add(new NodeEntry<LocalizedText>("Description", node.getDescription(), node));
		list.add(new NodeEntry<LocalizedText>("DisplayName", node.getDisplayName(), node));
		list.add(new NodeEntry<NodeClass>("NodeClass", node.getNodeClass(), node));
		list.add(new NodeEntry<NodeId>("NodeId", node.getNodeId(), node));
		list.add(new NodeEntry<UInteger>("UserWriteMask", node.getUserWriteMask(), node));
		list.add(new NodeEntry<UInteger>("WriteMask", node.getWriteMask(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedDataTypeNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<Boolean>("IsAbstract", node.isAbstract(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedMethodNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<Boolean>("Executable", node.isExecutable(), node));
		list.add(new NodeEntry<Boolean>("UserExecutable", node.isUserExecutable(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedObjectNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<UByte>("EventNotifier", node.getEventNotifier(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedObjectTypeNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<Boolean>("IsAbstract", node.isAbstract(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedReferenceTypeNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<Boolean>("IsAbstract", node.isAbstract(), node));
		list.add(new NodeEntry<Boolean>("Symmetric", node.isSymmetric(), node));
		list.add(new NodeEntry<LocalizedText>("InverseName", node.getInverseName(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedVariableNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<Object>("Value", node.getValue(), node));
		list.add(new NodeEntry<NodeId>("DataType", node.getDataType(), node));
		list.add(new NodeEntry<Integer>("ValueRank", node.getValueRank(), node));
		list.add(new NodeEntry<UInteger[]>("ArrayDimensions", node.getArrayDimensions(), node));
		list.add(new NodeEntry<UByte>("AccessLevel", node.getAccessLevel(), node));
		list.add(new NodeEntry<UByte>("UserAccessLevel", node.getUserAccessLevel(), node));
		list.add(new NodeEntry<Double>("MinimumSamplingInterval", node.getMinimumSamplingInterval(), node));
		list.add(new NodeEntry<Boolean>("Historizing", node.isHistorizing(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedVariableTypeNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<Object>("Value", node.getValue(), node));
		list.add(new NodeEntry<NodeId>("DataType", node.getDataType(), node));
		list.add(new NodeEntry<Integer>("ValueRank", node.getValueRank(), node));
		list.add(new NodeEntry<UInteger[]>("ArrayDimensions", node.getArrayDimensions(), node));
		list.add(new NodeEntry<Boolean>("IsAbstract", node.isAbstract(), node));

		return list;
	}

	public static Iterable<NodeEntry<?>> fromNode(CachedViewNode node) {
		ArrayList<NodeEntry<?>> list = (ArrayList<NodeEntry<?>>) fromBaseNode((CachedBaseNode) node);

		list.add(new NodeEntry<UByte>("EventNotifier", node.getEventNotifier(), node));
		list.add(new NodeEntry<Boolean>("ContainsNoLoop", node.isContainsNoLoop(), node));

		return list;
	}

	public static class NodeEntry<T> {

		private final String text;
		private final String typeName;
		private T value;
		private final CachedBaseNode parentNode;

		private NodeEntry(String text, T value, CachedBaseNode parent) {
			this.text = text;
			this.value = value;
			this.typeName = value!=null?value.getClass().getSimpleName():"";
			this.parentNode = parent;
		}

		public String getText() {
			return text;
		}

		public String getTypeName() {
			return typeName;
		}

		public CachedBaseNode getCachedNode() {
			return parentNode;
		}

		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}
}
