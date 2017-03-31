package me.steffenjacobs.opcuadisplay.shared.domain;

import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.nodes.UaMethodNode;

import me.steffenjacobs.opcuadisplay.shared.util.FutureResolver;

public class CachedMethodNode extends CachedBaseNode {

	private boolean executable;
	private boolean userExecutable;

	public CachedMethodNode(UaMethodNode node) throws InterruptedException, ExecutionException {
		super(node);

		executable = FutureResolver.resolveFutureSafe(node.getExecutable());
		userExecutable = FutureResolver.resolveFutureSafe(node.getUserExecutable());
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
}
