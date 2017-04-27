package me.steffenjacobs.opcuadisplay.shared.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.IdType;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;

public class SharedStorage {

	private static SharedStorage instance;
	private final Map<SharedField, Object> variables = new ConcurrentHashMap<>();

	private SharedStorage() {
		// singleton
	}

	public static SharedStorage getInstance() {
		if (instance == null) {
			instance = new SharedStorage();
		}
		return instance;
	}

	public void setValue(SharedField key, Object value) {
		variables.put(key, value);
	}

	public Object getValue(SharedField key) {
		return variables.get(key);
	}
	
	public void removeValue(SharedField key){
		variables.remove(key);
	}

	public void increaseHighestNodeIdIfNecessarySafe(CachedBaseNode cn) {

		if (cn == null || cn.getNodeId() == null || cn.getNodeId().getIdentifier() == null
				|| cn.getNodeId().getType() != IdType.Numeric) {
			return;
		}
		final int nodeId;
		if (cn.getNodeId().getIdentifier() instanceof UInteger) {
			nodeId = ((UInteger) cn.getNodeId().getIdentifier()).intValue();
		} else if (cn.getNodeId().getIdentifier() instanceof Integer) {
			nodeId = (int) cn.getNodeId().getIdentifier();
		} else {
			return;
		}

		variables.putIfAbsent(SharedField.HighestNodeId, nodeId);
		variables.computeIfPresent(SharedField.HighestNodeId, (k, v) -> v = (Integer) v > nodeId ? v : nodeId);
	}
	
	public CachedBaseNode getRoot(){
		return (CachedBaseNode) getValue(SharedField.RootNode);
	}

	public static enum SharedField {
		HighestNodeId, RootNode;
	}
}
