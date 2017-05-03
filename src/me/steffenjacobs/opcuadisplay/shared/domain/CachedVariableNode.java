package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedVariableNode extends CachedBaseNode implements HasValueRank{

	private Object value;
	private NodeId dataType;
	private int valueRank;
	private UInteger[] arrayDimensions;
	private UByte accessLevel;
	private UByte userAccessLevel;
	private double minimumSamplingInterval;
	private boolean historizing;

	public CachedVariableNode(UaVariableNode node) throws InterruptedException, ExecutionException {
		super(node);

		value = FutureResolver.resolveFutureSafe(node.getValue());
		dataType = FutureResolver.resolveFutureSafe(node.getDataType());
		valueRank = FutureResolver.resolveFutureSafe(node.getValueRank());
		arrayDimensions = FutureResolver.resolveFutureSafe(node.getArrayDimensions());
		accessLevel = FutureResolver.resolveFutureSafe(node.getAccessLevel());
		userAccessLevel = FutureResolver.resolveFutureSafe(node.getUserAccessLevel());
		minimumSamplingInterval = FutureResolver.resolveFutureSafe(node.getMinimumSamplingInterval());
		historizing = FutureResolver.resolveFutureSafe(node.getHistorizing());
	}

	protected CachedVariableNode(CachedVariableNode node) {
		super(node);
		this.value = node.value;
		this.dataType = node.dataType;
		this.valueRank = node.valueRank;
		this.arrayDimensions = node.arrayDimensions.clone();
		this.accessLevel = UByte.valueOf(node.accessLevel.intValue());
		this.userAccessLevel = UByte.valueOf(node.userAccessLevel.intValue());
		this.minimumSamplingInterval = node.minimumSamplingInterval;
		this.historizing = node.historizing;
	}

	@Override
	public CachedVariableNode duplicate() {
		return new CachedVariableNode(this);
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

	public UByte getAccessLevel() {
		return accessLevel;
	}

	public void setAccessLevel(UByte accessLevel) {
		this.accessLevel = accessLevel;
	}

	public UByte getUserAccessLevel() {
		return userAccessLevel;
	}

	public void setUserAccessLevel(UByte userAccessLevel) {
		this.userAccessLevel = userAccessLevel;
	}

	public double getMinimumSamplingInterval() {
		return minimumSamplingInterval;
	}

	public void setMinimumSamplingInterval(double minimumSamplingInterval) {
		this.minimumSamplingInterval = minimumSamplingInterval;
	}

	public boolean isHistorizing() {
		return historizing;
	}

	public void setHistorizing(boolean historizing) {
		this.historizing = historizing;
	}
}
