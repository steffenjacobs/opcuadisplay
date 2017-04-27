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

import org.eclipse.core.runtime.IProgressMonitor;
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

	private final HashMap<NodeId, String> typeNamesCache = new HashMap<>();

	/**
	 * @return all nodes from an opc server specified in <i>url</i> linked to
	 *         their parents
	 */
	public CachedBaseNode retrieveNodes(String url, final IProgressMonitor monitor) throws Exception {

		monitor.beginTask("Establishing connection with " + url + "...", 2);

		OpcUaClient client = createClient(url);

		monitor.worked(1);
		// synchronous connect
		client.connect().get();

		// start browsing at root folder
		long start = System.currentTimeMillis();
		ExecutorService exec = Executors.newFixedThreadPool(8);

		monitor.worked(1);

		monitor.beginTask("Downloading Models...", 100);
		// receive sub folders of root
		final CachedBaseNode root = retrieveNodes(CachedBaseNode.createNewRoot(), client, false);

		toList(root.getChildren()).forEach(c -> {
			// Objects
			if (c.getNodeId().equals(Identifiers.ObjectsFolder)) {
				// retrieve objects async
				exec.submit(() -> retrieveNodesMonitored(c, client, true, monitor, 20));
			}
			// Types
			else if (c.getNodeId().equals(Identifiers.TypesFolder)) {
				// retrieve sub types
				CachedBaseNode typeNode = retrieveNodes(c, client, false);
				toList(typeNode.getChildren()).forEach(tc -> {
					// Data Types
					if (Identifiers.DataTypesFolder.equals(tc.getNodeId())) {
						// retrieve sub types of DataTypes
						CachedBaseNode dataType = retrieveNodes(tc, client, false);

						// retrieve each sub type of DataTypes async
						toList(dataType.getChildren()).forEach(dtc -> {
							exec.submit(() -> retrieveNodesMonitored(dtc, client, true, monitor, 26));
						});
					}
					// EventTypes, ObjectTypes, ReferenceTypes, VariableTypes
					else {
						retrieveNodes(tc, client, true);
					}
				});
			}
			// Views
			else if (c.getNodeId().equals(Identifiers.ViewsFolder)) {
				retrieveNodes(c, client, true);
			}
		});

		exec.shutdown();
		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		logger.info("download complete (" + (System.currentTimeMillis() - start) + "ms). ");

		// disconnect
		client.disconnect();

		monitor.worked(2);

		return root;
	}

	private void retrieveNodesMonitored(final CachedBaseNode parent, OpcUaClient client, boolean recursive,
			IProgressMonitor monitor, int value) {
		retrieveNodes(parent, client, recursive);
		monitor.worked(value);
	}

	/** @return true, if <i>node</i> is in the Types folder. */
	private boolean isInTypesFolder(CachedBaseNode node) {

		while ((node = node.getParent()) != null) {
			if (Identifiers.TypesFolder.equals(node.getNodeId())) {
				return true;
			}
		}

		return false;

	}

	/** @returns a list of all references associated with <i>node</i> */
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

	/**
	 * @return the name of the node associated to <i>id</i>. If the NodeId had
	 *         already been queried, the associated name had been cached.
	 */
	public String getNameOfNode(NodeId id, OpcUaClient client) {
		if (id == null) {
			return "null";
		}

		// check if cached
		if (typeNamesCache.containsKey(id)) {
			return typeNamesCache.get(id);
		}

		// retrieve node
		CachedBaseNode node = retrieveNodeDetails(id, client);

		if (node == null) {
			return "null";
		}
		if (node.getDisplayName() == null) {
			return "null";
		}
		typeNamesCache.put(id, node.getDisplayName().getText());

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

				CachedBaseNode cbn = retrieveNodeDetails(rd.getNodeId().local().orElse(null), client);
				if (cbn != null) {
					browseReferencesRecursive(cbn, client).forEach(nd -> cbn.addChild(nd));
					ref.add(cbn);
				}
				else{
					ref.add(new CachedBaseNode(rd));
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

	/** retrieves the attributes of a node associated to <i>nodeId</i> */
	private CachedBaseNode retrieveNodeDetails(NodeId nodeId, OpcUaClient client) {
		UaNode node = null;
		try {
			node = client.getAddressSpace().getNodeInstance(nodeId).get();

			return parseNode(node);
		} catch (InterruptedException | ExecutionException e) {
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param node
	 *            the node to parse
	 * @return the cached node of the correct class of <i>node</i>
	 */
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

	/**
	 * retrieves the child nodes of <i>parent</i>
	 * 
	 * @param parent
	 *            the parent which children should be retrieved
	 * @param client
	 *            the OpcUaClient which is holding the connection open
	 * @param recursive
	 *            whether the child nodes should be retrieved recursivelyS
	 * 
	 * 
	 * @return <i>parent</i> with the associated children linked to it
	 */
	private CachedBaseNode retrieveNodes(final CachedBaseNode parent, OpcUaClient client, boolean recursive) {
		try {
			// TODO: retrieve root, if necessary

			// retrieve node
			client.getAddressSpace().browse(parent.getNodeId()).get().forEach(node -> {

				try {
					// cache node
					CachedBaseNode cn = parseNode(node);

					// set parent
					cn.setParent(parent);

					// references
					// if in type folder, retrieve all references recursively
					// and add them as children
					if (isInTypesFolder(cn)) {
						browseReferencesRecursive(cn, client)
								.forEach(ref -> cn.addChild(retrieveNodes(ref, client, true)));
					}
					cn.setReferences(browseAllReferences(cn, client));

					// retrieve children
					if (recursive) {
						retrieveNodes(cn, client, recursive);
					}

					// add child to parent
					parent.addChild(cn);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

			});
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing nodeId={} failed: {}", parent.getNodeId(), e.getMessage(), e);
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
