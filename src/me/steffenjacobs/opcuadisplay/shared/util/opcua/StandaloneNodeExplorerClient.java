package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.client.nodes.UaDataTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaObjectTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaReferenceTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableTypeNode;
import org.eclipse.milo.opcua.sdk.client.nodes.UaViewNode;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseResult;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReferenceTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedViewNode;

public class StandaloneNodeExplorerClient {

	private static final Logger logger = LoggerFactory.getLogger(StandaloneNodeExplorerClient.class);

	private final HashMap<NodeId, String> typeNames = new HashMap<>();

	public static void main(String[] args) throws Exception {
		new StandaloneNodeExplorerClient().retrieveNodes("opc.tcp://localhost:12686/example");
	}

	public CachedBaseNode retrieveNodes(String url) throws Exception {

		OpcUaClient client = createClient(url);

		// synchronous connect
		client.connect().get();

		// start browsing at root folder
		long start = System.currentTimeMillis();
		ExecutorService exec = Executors.newFixedThreadPool(8);

		final CachedBaseNode root = retrieveSubNodes(CachedBaseNode.createNewRoot(), client, Identifiers.RootFolder);

		toList(root.getChildren()).forEach(c -> {
			if (c.getNodeId().equals(Identifiers.ObjectsFolder)) {
				exec.submit(() -> retrieveNodes(c, client, Identifiers.ObjectsFolder));
			} else if (c.getNodeId().equals(Identifiers.TypesFolder)) {
				CachedBaseNode typeNode = retrieveSubNodes(c, client, Identifiers.TypesFolder);
				toList(typeNode.getChildren()).forEach(tc -> {
					if (Identifiers.DataTypesFolder.equals(tc.getNodeId())) {
						CachedBaseNode dataType = retrieveSubNodes(tc, client, tc.getNodeId());

						toList(dataType.getChildren()).forEach(dtc -> {
							exec.submit(() -> retrieveNodes(dtc, client, dtc.getNodeId()));
						});
					} else {
						retrieveNodes(tc, client, tc.getNodeId());
					}
				});
			} else if (c.getNodeId().equals(Identifiers.ViewsFolder)) {
				retrieveNodes(c, client, Identifiers.ViewsFolder);
			}
		});

		exec.shutdown();
		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		System.out.println("download complete (" + (System.currentTimeMillis() - start) + "ms). ");

		// disconnect
		client.disconnect();

		return root;
	}

	private boolean isInTypesFolder(CachedBaseNode node) {

		while ((node = node.getParent()) != null) {
			if (Identifiers.TypesFolder.equals(node.getNodeId())) {
				return true;
			}
		}

		return false;

	}

	private List<CachedReference> browseAllReferences(CachedBaseNode node, OpcUaClient client) {
		List<CachedReference> ref = new ArrayList<>();
		try {
			BrowseDescription browse = new BrowseDescription(node.getNodeId(), BrowseDirection.Forward,
					Identifiers.References, true,
					uint(NodeClass.Object.getValue() | NodeClass.DataType.getValue() | NodeClass.ObjectType.getValue()
							| NodeClass.VariableType.getValue() | NodeClass.ReferenceType.getValue()
							| NodeClass.Method.getValue() | NodeClass.Variable.getValue()),

					uint(BrowseResultMask.All.getValue()));

			BrowseResult browseResult = client.browse(browse).get();

			toList(browseResult.getReferences()).forEach(rd -> {

				CachedReference cr = new CachedReference(getNameOfNode(rd.getReferenceTypeId(), client),
						rd.getBrowseName(), getNameOfNode(rd.getTypeDefinition().local().orElse(null), client),
						rd.getNodeId().local().get());
				ref.add(cr);

			});
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing references for nodeId={} failed: {}", node, e.getMessage(), e);
		}

		return ref;
	}

	public String getNameOfNode(NodeId id, OpcUaClient client) {
		if (id == null) {
			return "null";
		}

		if (typeNames.containsKey(id)) {
			return typeNames.get(id);
		}

		CachedBaseNode node = retrieveTypeNode(id, client);

		if (node == null) {
			return "null";
		}
		if (node.getDisplayName() == null) {
			return "null";
		}
		typeNames.put(id, node.getDisplayName().getText());

		return node.getDisplayName().getText();
	}

	private List<CachedBaseNode> browseReferencesRecursive(CachedBaseNode node, OpcUaClient client) {
		List<CachedBaseNode> ref = new ArrayList<>();
		try {
			BrowseDescription browse = new BrowseDescription(node.getNodeId(), BrowseDirection.Forward,
					Identifiers.References, true,
					uint(NodeClass.Object.getValue() | NodeClass.DataType.getValue() | NodeClass.ObjectType.getValue()
							| NodeClass.VariableType.getValue() | NodeClass.ReferenceType.getValue()
							| NodeClass.Method.getValue() | NodeClass.Variable.getValue()),

					uint(BrowseResultMask.All.getValue()));

			BrowseResult browseResult = client.browse(browse).get();

			final List<CachedReference> refs = new ArrayList<>();
			toList(browseResult.getReferences()).forEach(rd -> {

				CachedBaseNode cbn = retrieveTypeNode(rd.getNodeId().local().orElse(null), client);
				if (cbn != null) {
					browseReferencesRecursive(cbn, client).forEach(nd -> cbn.addChild(nd));
					ref.add(cbn);
				}

				refs.add(new CachedReference(getNameOfNode(rd.getReferenceTypeId(), client), rd.getBrowseName(),
						getNameOfNode(rd.getTypeDefinition().local().orElse(null), client),
						rd.getNodeId().local().get()));
			});

			node.setReferences(refs);
		} catch (InterruptedException | ExecutionException | NullPointerException e) {
			logger.error("Browsing references for nodeId={} failed: {}", node, e.getMessage(), e);
		}
		return ref;
	}

	private List<CachedBaseNode> browseSubReferences(CachedBaseNode node, OpcUaClient client) {
		List<CachedBaseNode> ref = new ArrayList<>();
		try {
			BrowseDescription browse = new BrowseDescription(node.getNodeId(), BrowseDirection.Forward,
					Identifiers.References, true,
					uint(NodeClass.Object.getValue() | NodeClass.DataType.getValue() | NodeClass.ObjectType.getValue()
							| NodeClass.VariableType.getValue() | NodeClass.ReferenceType.getValue()
							| NodeClass.Method.getValue() | NodeClass.Variable.getValue()),

					uint(BrowseResultMask.All.getValue()));

			BrowseResult browseResult = client.browse(browse).get();

			final List<CachedReference> refs = new ArrayList<>();
			toList(browseResult.getReferences()).forEach(rd -> {

				CachedBaseNode cbn = retrieveTypeNode(rd.getNodeId().local().orElse(null), client);
				if (cbn != null) {
					ref.add(cbn);
				}

				refs.add(new CachedReference(getNameOfNode(rd.getReferenceTypeId(), client), rd.getBrowseName(),
						getNameOfNode(rd.getTypeDefinition().local().orElse(null), client),
						rd.getNodeId().local().get()));
			});

			node.setReferences(refs);
		} catch (InterruptedException | ExecutionException | NullPointerException e) {
			logger.error("Browsing references for nodeId={} failed: {}", node, e.getMessage(), e);
		}
		return ref;
	}

	private CachedBaseNode retrieveTypeNode(NodeId nodeId, OpcUaClient client) {
		UaNode node = null;
		try {
			node = client.getAddressSpace().getNodeInstance(nodeId).get();

			return parseNode(node);
		} catch (InterruptedException | ExecutionException e) {
			// e.printStackTrace();
		}
		return null;
	}

	private CachedBaseNode parseNode(Node node) throws InterruptedException, ExecutionException {
		if (node instanceof UaDataTypeNode) {
			return new CachedDataTypeNode((UaDataTypeNode) node);
		} else if (node instanceof UaMethodNode) {
			return new CachedMethodNode((UaMethodNode) node);
		} else if (node instanceof UaObjectNode) {
			return new CachedObjectNode((UaObjectNode) node);
		} else if (node instanceof UaObjectTypeNode) {
			return new CachedObjectTypeNode((UaObjectTypeNode) node);
		} else if (node instanceof UaReferenceTypeNode) {
			return new CachedReferenceTypeNode((UaReferenceTypeNode) node);
		} else if (node instanceof UaVariableNode) {
			return new CachedVariableNode((UaVariableNode) node);
		} else if (node instanceof UaVariableTypeNode) {
			return new CachedVariableTypeNode((UaVariableTypeNode) node);
		} else if (node instanceof UaViewNode) {
			return new CachedViewNode((UaViewNode) node);
		} else {
			return new CachedBaseNode(node);
		}
	}

	private CachedBaseNode retrieveNodes(final CachedBaseNode parent, OpcUaClient client, NodeId browseRoot) {
		try {
			// TODO: retrieve root, if necessary

			// retrieve node
			client.getAddressSpace().browse(browseRoot).get().forEach(node -> {

				try {
					// cache node
					CachedBaseNode cn = parseNode(node);

					// set parent
					cn.setParent(parent);

					// references
					if (isInTypesFolder(cn)) {
						browseReferencesRecursive(cn, client)
								.forEach(ref -> cn.addChild(retrieveNodes(ref, client, ref.getNodeId())));
					}
					cn.setReferences(browseAllReferences(cn, client));

					// retrieve children
					retrieveNodes(cn, client, cn.getNodeId());

					// add child to parent
					parent.addChild(cn);
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing nodeId={} failed: {}", browseRoot, e.getMessage(), e);
		}

		return parent;
	}

	private CachedBaseNode retrieveSubNodes(final CachedBaseNode parent, OpcUaClient client, NodeId browseRoot) {
		try {
			// TODO: retrieve root, if necessary

			// retrieve node
			client.getAddressSpace().browse(browseRoot).get().forEach(node -> {

				try {
					// cache node
					CachedBaseNode cn = parseNode(node);

					// set parent
					cn.setParent(parent);

					// references
					if (isInTypesFolder(cn)) {
						browseReferencesRecursive(cn, client)
								.forEach(ref -> cn.addChild(retrieveNodes(ref, client, ref.getNodeId())));
					}
					cn.setReferences(browseAllReferences(cn, client));

					// retrieve children
					// retrieveNodes(cn, client, cn.getNodeId());

					// add child to parent
					parent.addChild(cn);
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			});
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing nodeId={} failed: {}", browseRoot, e.getMessage(), e);
		}

		return parent;
	}

	private static OpcUaClient createClient(String url) throws Exception {

		SecurityPolicy securityPolicy = SecurityPolicy.None;

		// initialize endpoint
		EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(url).get();

		EndpointDescription endpoint = Arrays.stream(endpoints)
				.filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri())).findFirst()
				.orElseThrow(() -> new Exception("no desired endpoints returned"));

		logger.info("Using endpoint: {} [{}]", endpoint.getEndpointUrl(), securityPolicy);

		// load keystore
		KeyStoreLoader loader = new KeyStoreLoader();
		loader.load();

		// create config
		OpcUaClientConfig opcConfig = OpcUaClientConfig.builder()
				.setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
				.setApplicationUri("urn:eclipse:milo:examples:client").setCertificate(loader.getClientCertificate())
				.setKeyPair(loader.getClientKeyPair()).setEndpoint(endpoint)
				.setIdentityProvider(new AnonymousProvider()).setRequestTimeout(Unsigned.uint(5000)).build();

		return new OpcUaClient(opcConfig);
	}
}
