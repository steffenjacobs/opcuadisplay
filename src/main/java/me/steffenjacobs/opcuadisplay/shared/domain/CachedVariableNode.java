package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.opcua.FutureResolver;
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeNavigator;

/** @author Steffen Jacobs */
public class CachedVariableNode extends CachedBaseNode implements HasValueRank {

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
		this.arrayDimensions = node.arrayDimensions != null ? node.arrayDimensions.clone() : new UInteger[0];
		this.accessLevel = UByte.valueOf(node.accessLevel != null ? node.accessLevel.intValue() : 0);
		this.userAccessLevel = UByte.valueOf(node.accessLevel != null ? node.userAccessLevel.intValue() : 0);
		this.minimumSamplingInterval = node.minimumSamplingInterval;
		this.historizing = node.historizing;
	}

	public CachedVariableNode(NodeId nodeId) {
		super(nodeId, NodeClass.Variable);
		this.dataType = new NodeId(0, 0);
		this.arrayDimensions = new UInteger[0];
		this.accessLevel = UByte.valueOf(0);
		this.userAccessLevel = UByte.valueOf(0);
	}

	public static CachedVariableNode create(int namespaceIndex, String name, int nodeId, CachedDataTypeNode type) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedVariableNode cvn = new CachedVariableNode(id);
		cvn.setDisplayName(new LocalizedText("en", name));
		cvn.setBrowseName(new QualifiedName(namespaceIndex, name));

		cvn.dataType = type.getNodeId();

		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(cvn);

		for (CachedBaseNode child : type.getChildren()) {
			cvn.addChild(child);
			child.setParent(cvn);
		}

		// rewire references & duplicate children recursive
		return cvn.duplicate();
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
