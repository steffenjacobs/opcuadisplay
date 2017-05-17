package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedViewNode;
/** @author Steffen Jacobs */
public class NodeMerger {
	private static NodeMerger instance;

	private NodeMerger() {
		// singleton
	}

	public static NodeMerger getInstance() {
		if (instance == null) {
			instance = new NodeMerger();
		}
		return instance;
	}

	/** merge inherited excluding children and parent */
	public CachedBaseNode merge(CachedBaseNode original, CachedBaseNode merger) {

		original.setBrowseName(merger.getBrowseName());
		// original.setChildren(Lists.newArrayList(merger.getChildren()));
		original.setDescription(merger.getDescription());
		original.setDisplayName(merger.getDisplayName());
		original.setNodeClass(merger.getNodeClass());
		// original.setParent(merger.getParent());
		original.setReferences(merger.getReferences());
		original.setUserWriteMask(merger.getUserWriteMask());
		original.setWriteMask(merger.getWriteMask());

		if (!merger.getClass().getName().equals(original.getClass().getName())) {
			return original;
		}

		// DataType
		if (original instanceof CachedDataTypeNode) {
			((CachedDataTypeNode) original).setAbstract(((CachedDataTypeNode) merger).isAbstract());
		}
		// Method
		else if (original instanceof CachedMethodNode) {
			((CachedMethodNode) original).setExecutable(((CachedMethodNode) merger).isExecutable());
			((CachedMethodNode) original).setUserExecutable(((CachedMethodNode) merger).isUserExecutable());
		}
		// Object
		else if (original instanceof CachedObjectNode) {
			((CachedObjectNode) original).setEventNotifier(((CachedObjectNode) merger).getEventNotifier());
		}
		// ObjectType
		else if (original instanceof CachedDataTypeNode) {
			((CachedObjectTypeNode) original).setAbstract(((CachedObjectTypeNode) merger).isAbstract());
		}
		// Variable
		else if (original instanceof CachedVariableNode) {
			((CachedVariableNode) original).setValue(((CachedVariableNode) merger).getValue());
			((CachedVariableNode) original).setDataType(((CachedVariableNode) merger).getDataType());
			((CachedVariableNode) original).setValueRank(((CachedVariableNode) merger).getValueRank());
			((CachedVariableNode) original).setArrayDimensions(((CachedVariableNode) merger).getArrayDimensions());
			((CachedVariableNode) original).setAccessLevel(((CachedVariableNode) merger).getAccessLevel());
			((CachedVariableNode) original).setUserAccessLevel(((CachedVariableNode) merger).getUserAccessLevel());
			((CachedVariableNode) original)
					.setMinimumSamplingInterval(((CachedVariableNode) merger).getMinimumSamplingInterval());
			((CachedVariableNode) original).setHistorizing(((CachedVariableNode) merger).isHistorizing());
		}
		// VariableType
		else if (original instanceof CachedVariableTypeNode) {
			((CachedVariableTypeNode) original).setValue(((CachedVariableTypeNode) merger).getValue());
			((CachedVariableTypeNode) original).setDataType(((CachedVariableTypeNode) merger).getDataType());
			((CachedVariableTypeNode) original).setValueRank(((CachedVariableTypeNode) merger).getValueRank());
			((CachedVariableTypeNode) original)
					.setArrayDimensions(((CachedVariableTypeNode) merger).getArrayDimensions());
			((CachedVariableTypeNode) original).setAbstract(((CachedVariableTypeNode) merger).isAbstract());
		}
		// View
		else if (original instanceof CachedViewNode) {
			((CachedViewNode) original).setContainsNoLoop(((CachedViewNode) merger).isContainsNoLoop());
			((CachedViewNode) original).setEventNotifier(((CachedViewNode) merger).getEventNotifier());
		}

		return original;
	}
}
