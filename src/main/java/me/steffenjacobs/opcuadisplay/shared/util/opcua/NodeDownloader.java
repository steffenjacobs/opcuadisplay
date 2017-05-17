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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
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

public class NodeDownloader {

	private static final Logger logger = LoggerFactory.getLogger(NodeDownloader.class);

	private final HashMap<NodeId, String> typeNamesCache = new HashMap<>();

	public CachedBaseNode retrieveNodes(String url, final IProgressMonitor monitor) throws Exception {
		NodeNavigator.getInstance().resetHighestNodeId();

		monitor.beginTask("Establishing connection with " + url + "...", 2);

		OpcUaClient client = createClient(url);

		if (client == null) {
			return CachedBaseNode.getDummyNoData();
		}

		monitor.worked(1);
		// synchronous connect
		client.connect().get();

		// start browsing at root folder
		long start = System.currentTimeMillis();
		ExecutorService exec = Executors.newFixedThreadPool(8);

		monitor.worked(1);

		monitor.beginTask("Downloading Models...", 100);
		// receive sub folders of root
		final CachedBaseNode root = retrieveNodes(NodeGenerator.getInstance().generateRoot(), client, false);

		toList(root.getChildren()).forEach(root_xxx -> {

			// Objects
			if (root_xxx.getNodeId().equals(Identifiers.ObjectsFolder)) {
				// retrieve objects async
				exec.submit(() -> retrieveNodesMonitored(root_xxx, client, true, monitor, 20));
			}
			// Types
			else if (root_xxx.getNodeId().equals(Identifiers.TypesFolder)) {
				// retrieve sub types
				retrieveNodes(root_xxx, client, false);

				toList(root_xxx.getChildren()).forEach(root_types_xxx -> {

					// Data Types
					if (Identifiers.DataTypesFolder.equals(root_types_xxx.getNodeId())) {
						// retrieve sub types of DataTypes

						// clear children, because they were not recursive and
						// will now be overwritten anyway
						root_types_xxx.setChildren(new ArrayList<>());
						retrieveNodes(root_types_xxx, client, false);

						// retrieve each sub type of DataTypes async
						toList(root_types_xxx.getChildren()).forEach(root_types_datatypes_xxx -> {
							// clear children, because they were not
							// recursive and will now be overwritten anyway
							root_types_datatypes_xxx.setChildren(new ArrayList<>());

							exec.submit(() -> {
								retrieveNodesMonitored(root_types_datatypes_xxx, client, true, monitor, 26);
							});
						});

					}
					// EventTypes, ObjectTypes, ReferenceTypes, VariableTypes
					else {

						// clear children, because they were not recursive and
						// will now be overwritten anyway
						root_types_xxx.setChildren(new ArrayList<>());
						retrieveNodes(root_types_xxx, client, true);
					}
				});
			}
			// Views
			else if (root_xxx.getNodeId().equals(Identifiers.ViewsFolder)) {
				retrieveNodes(root_xxx, client, true);
			}
		});

		exec.shutdown();
		exec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		logger.info("download complete (" + (System.currentTimeMillis() - start) + "ms). Highest Node Id: "
				+ NodeNavigator.getInstance().getHighestNodeId());

		// disconnect
		client.disconnect();

		monitor.worked(2);

		return root;
	}

	private CachedBaseNode retrieveNodes(CachedBaseNode parent, OpcUaClient client, boolean recursive) {
		BrowseDescription browse = new BrowseDescription(parent.getNodeId(), BrowseDirection.Forward,
				Identifiers.References, true,
				uint(NodeClass.Object.getValue() | NodeClass.DataType.getValue() | NodeClass.ObjectType.getValue()
						| NodeClass.VariableType.getValue() | NodeClass.ReferenceType.getValue()
						| NodeClass.Method.getValue() | NodeClass.Variable.getValue()),

				uint(BrowseResultMask.All.getValue()));

		try {
			BrowseResult browseResult = client.browse(browse).get();

			final List<CachedReference> refs = new ArrayList<>();
			toList(browseResult.getReferences()).forEach(rd -> {

				if ("HasTypeDefinition".equals(getNameOfNode(parent.getNodeId(), rd.getReferenceTypeId(), client))) {
					return;
				}

				// retrieve the node details from the reference
				CachedBaseNode cbn = retrieveNodeDetails(parent.getNodeId(), rd.getNodeId().local().orElse(null),
						client);

				if (cbn == null) {
					System.out.println(getNameOfNode(null, rd.getTypeId(), client));
					System.out.println(rd.getBrowseName().getName());
					System.out.println(rd.getNodeId().local().orElse(null));
					System.out.println(getNameOfNode(parent.getNodeId(), rd.getNodeId().local().orElse(null), client));
					System.out.println(getNameOfNode(parent.getNodeId(), parent.getNodeId(), client));
				}

				// add references
				refs.add(new CachedReference(getNameOfNode(parent.getNodeId(), rd.getReferenceTypeId(), client),
						rd.getBrowseName(),
						getNameOfNode(parent.getNodeId(), rd.getTypeDefinition().local().orElse(null), client),
						rd.getNodeId().local().get()));

				// recursion
				if (recursive) {
					System.out.println("rec for " + cbn.getBrowseName().getName());
					cbn = retrieveNodes(cbn, client, recursive);
				}

				addChildToNode(parent, cbn);
			});

			parent.setReferences(refs);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parent;
	}

	/**
	 * @return the name of the node associated to <i>id</i>. If the NodeId had
	 *         already been queried, the associated name had been cached.
	 */
	public String getNameOfNode(NodeId parentNodeId, NodeId id, OpcUaClient client) {
		if (id == null) {
			return "null";
		}

		// check if cached
		if (typeNamesCache.containsKey(id)) {
			return typeNamesCache.get(id);
		}

		// retrieve node
		CachedBaseNode node = retrieveNodeDetails(parentNodeId, id, client);

		if (node == null) {
			return "null";
		}
		if (node.getDisplayName() == null) {
			return "[BROWSE]" + node.getBrowseName().getName();
		}
		typeNamesCache.put(id, node.getDisplayName().getText());

		return node.getDisplayName().getText();
	}

	/**
	 * adds a child to the parent without auto-referencing and adjusts the
	 * NodeId of the child, if necessary
	 */
	private void addChildToNode(CachedBaseNode parent, CachedBaseNode child) {
		parent.addChild(child);
		child.setParent(parent);
		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(child);
	}

	/** retrieves the attributes of a node associated to <i>nodeId</i> */
	private CachedBaseNode retrieveNodeDetails(NodeId parentId, NodeId nodeId, OpcUaClient client) {
		Node node = null;
		try {
			node = client.getAddressSpace().getNodeInstance(nodeId).get();

		} catch (InterruptedException | ExecutionException e) {
			// this is normal, if the requested NodeId had not been found
		}

		try {
			if (node == null) {
				List<Node> lst = client.getAddressSpace().browse(parentId).get();
				node = lst.stream().filter(x -> {
					try {
						return x.getNodeId().get().equals(nodeId);
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					return false;
				}).findAny().orElse(null);
			}

			return parseNode(node);
		} catch (InterruptedException | ExecutionException e) {
			// this is normal, if the requested NodeId had not been found
		}
		return null;
	}

	private void retrieveNodesMonitored(final CachedBaseNode parent, OpcUaClient client, boolean recursive,
			IProgressMonitor monitor, int value) {
		retrieveNodes(parent, client, recursive);
		monitor.worked(value);
	}

	/**
	 * @param node
	 *            the node to parse
	 * @return the cached node of the correct class of <i>node</i>
	 */
	private CachedBaseNode parseNode(Node node) throws InterruptedException, ExecutionException {
		if (node == null) {
			return null;
		}

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

	private static void openMessageBox(final String title, final String message) {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				MessageBox box = new MessageBox(new Shell(), SWT.ICON_ERROR);
				box.setText(title);
				box.setMessage(message);
				box.open();
			}
		});
	}

	/**
	 * @return a preconfigured OpcUaClient not yet connected to <i>url</i> <br>
	 *         client.connect().get() will connect the client to <i>url</i>
	 */
	private static OpcUaClient createClient(String url) throws Exception {
		SecurityPolicy securityPolicy = SecurityPolicy.None;

		// initialize endpoint
		EndpointDescription[] endpoints;
		try {
			endpoints = UaTcpStackClient.getEndpoints(url).get();
		} catch (Exception excep) {
			if (excep.getMessage().startsWith("UaException: status=Bad_TcpEndpointUrlInvalid")) {
				openMessageBox("OPC UA Display", "Invalid hostname: " + url);
			} else {
				openMessageBox("OPC UA Display", excep.getLocalizedMessage());
			}
			return null;
		}

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
