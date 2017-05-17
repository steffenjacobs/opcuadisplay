package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;

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

	protected CachedVariableTypeNode(CachedVariableTypeNode node) {
		super(node);
		this.value = node.value;
		this.dataType = node.dataType;
		this.valueRank = node.valueRank;
		this.arrayDimensions = node.arrayDimensions!=null?node.arrayDimensions.clone():new UInteger[0];
		this.isAbstract = node.isAbstract;
	}

	public CachedVariableTypeNode(NodeId nodeId) {
		super(nodeId, NodeClass.VariableType);
		this.dataType = new NodeId(0, 0);
		this.arrayDimensions = new UInteger[0];
	}

	@Override
	public CachedVariableTypeNode duplicate() {
		return new CachedVariableTypeNode(this);
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

	public static CachedVariableTypeNode create(int namespaceIndex, String name, int nodeId, CachedVariableTypeNode type) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedVariableTypeNode cbn = new CachedVariableTypeNode(id);
		cbn.setDisplayName(new LocalizedText("en", name));
		cbn.setBrowseName(new QualifiedName(namespaceIndex, name));

		// set abstract to true per default
		cbn.setAbstract(true);

		cbn.getReferences()
				.add(new CachedReference("HasTypeDefinition", type.getBrowseName(), "null", type.getNodeId()));

		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(cbn);

		for (CachedBaseNode child : NodeNavigator.getInstance().aggregateInheritedChildren(type)) {
			NodeGenerator.getInstance().insertNode(child, cbn);
		}
		
		return  cbn.duplicate();
	}
}
