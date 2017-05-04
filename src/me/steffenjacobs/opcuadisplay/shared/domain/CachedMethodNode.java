package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaMethodNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.MethodArgument;

public class CachedMethodNode extends CachedBaseNode {

	private boolean executable;
	private boolean userExecutable;

	public CachedMethodNode(UaMethodNode node) throws InterruptedException, ExecutionException {
		super(node);

		executable = FutureResolver.resolveFutureSafe(node.getExecutable());
		userExecutable = FutureResolver.resolveFutureSafe(node.getUserExecutable());
	}

	protected CachedMethodNode(CachedMethodNode node) {
		super(node);
		this.executable = node.executable;
		this.userExecutable = node.userExecutable;
	}

	protected CachedMethodNode(NodeId nodeId) {
		super(nodeId, NodeClass.Method);
	}

	@Override
	public CachedMethodNode duplicate() {
		return new CachedMethodNode(this);
	}

	public boolean isExecutable() {
		return executable;
	}

	public void setExecutable(boolean executable) {
		this.executable = executable;
	}

	public boolean isUserExecutable() {
		return userExecutable;
	}

	public void setUserExecutable(boolean userExecutable) {
		this.userExecutable = userExecutable;
	}

	public static CachedMethodNode create(int namespaceIndex, String name, int nodeId, MethodArgument[] inputArgs,
			MethodArgument[] outputArgs) {
		NodeId id = new NodeId(namespaceIndex, nodeId);
		CachedMethodNode cmn = new CachedMethodNode(id);
		cmn.setDisplayName(new LocalizedText("en", name));
		cmn.setBrowseName(new QualifiedName(namespaceIndex, name));

		CachedDataTypeNode argumentType = (CachedDataTypeNode) NodeNavigator.getInstance()
				.navigateByName("Root/Types/DataTypes/BaseDataType/Structure/Argument");

		NodeGenerator.createProperty(namespaceIndex, "InputArguments", nodeId, argumentType, cmn);

		NodeGenerator.createProperty(namespaceIndex, "OutputArguments", nodeId, argumentType, cmn);

		// TODO: set arguments to value-property

		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(cmn);

		// rewire references & duplicate children recursive
		return cmn.duplicate();
	}
}
