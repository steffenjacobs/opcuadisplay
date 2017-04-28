package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedVariableTypeNode extends CachedBaseNode implements HasOnlyAbstract, HasValueRank {

	private Object value;
	private NodeId dataType;
	private int valueRank;
	private UInteger[] arrayDimensions;
	private boolean isAbstract;

	public CachedVariableTypeNode(UaVariableTypeNode node) throws InterruptedException, ExecutionException {
		super(node);

		value = FutureResolver.resolveFutureSafe(node.getValue());
		dataType = FutureResolver.resolveFutureSafe(node.getDataType());
		valueRank = FutureResolver.resolveFutureSafe(node.getValueRank());
		arrayDimensions = FutureResolver.resolveFutureSafe(node.getArrayDimensions());
		isAbstract = FutureResolver.resolveFutureSafe(node.getIsAbstract());
	}

	protected CachedVariableTypeNode(CachedVariableTypeNode node, NodeId nodeId) {
		super(node, nodeId);
		this.value = node.value;
		this.dataType = node.dataType;
		this.valueRank = node.valueRank;
		this.arrayDimensions = node.arrayDimensions.clone();
		this.isAbstract = node.isAbstract;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public NodeId getDataType() {
		return dataType;
	}

	public void setDataType(NodeId dataType) {
		this.dataType = dataType;
	}

	public int getValueRank() {
		return valueRank;
	}

	public void setValueRank(int valueRank) {
		this.valueRank = valueRank;
	}

	public UInteger[] getArrayDimensions() {
		return arrayDimensions;
	}

	public void setArrayDimensions(UInteger[] arrayDimensions) {
		this.arrayDimensions = arrayDimensions;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
}
