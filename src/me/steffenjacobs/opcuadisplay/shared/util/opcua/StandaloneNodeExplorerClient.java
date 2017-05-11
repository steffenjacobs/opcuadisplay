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
import java.util.stream.Collectors;

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
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
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

/** retrieves the nodes from the OPC UA server */
public class StandaloneNodeExplorerClient {

	private static final Logger logger = LoggerFactory.getLogger(StandaloneNodeExplorerClient.class);

	private final HashMap<NodeId, String> typeNamesCache = new HashMap<>();

	/**
	 * @return all nodes from an opc server specified in <i>url</i> linked to
	 *         their parents
	 */
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
		final CachedBaseNode root = retrieveNodes(CachedBaseNode.createNewRoot(), client, false);

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

	private void retrieveNodesMonitored(final CachedBaseNode parent, OpcUaClient client, boolean recursive,
			IProgressMonitor monitor, int value) {
		retrieveNodes(parent, client, recursive);
		monitor.worked(value);
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

				// pack the reference inside CachedReference
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

	/**
	 * browses the references recursive, by taking the referenced nodeid,
	 * retrieving the associated node and adding it to the parent recursively
	 */
	private List<CachedBaseNode> browseReferencesRecursive(CachedBaseNode node, OpcUaClient client, boolean recursive) {
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
				// retrieve the node details from the reference
				NodeId nodeId = rd.getNodeId().local().orElse(null);

				CachedBaseNode cbn = retrieveNodeDetails(nodeId, client);

				if (cbn != null) {
					// this is probably a type
					if (recursive) {
						browseReferencesRecursive(cbn, client, recursive).forEach(nd -> addChildToNode(cbn, nd));
					}
					ref.add(cbn);
				} else {
					// this is probably something else in the types folder (e.g.
					// objects, variables, etc.)

					CachedBaseNode nn;
					if (rd.getNodeClass() == NodeClass.Object) {
						nn = new CachedObjectNode(nodeId);
					} else if (rd.getNodeClass() == NodeClass.Variable) {

						nn = new CachedVariableNode(nodeId);
					} else if (rd.getNodeClass() == NodeClass.DataType) {
						nn = new CachedDataTypeNode(nodeId);
					} else {
						nn = new CachedBaseNode(nodeId, rd.getNodeClass());
					}

					nn.setBrowseName(rd.getBrowseName());
					nn.setDisplayName(rd.getDisplayName());

					ref.add(nn);
					nn.setReferences(browseAllReferences(nn, client));
					retrieveNodes(nn, client, recursive);
				}

				// add references
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
	private CachedBaseNode retrieveNodeDetails(NodeId nodeId, OpcUaClient client) {
		UaNode node = null;
		try {
			node = client.getAddressSpace().getNodeInstance(nodeId).get();

			return parseNode(node);
		} catch (InterruptedException | ExecutionException e) {
			// this is normal, if the requested NodeId had not been found
		}
		return null;
	}

	private CachedBaseNode retrieveNodeDetailsNonInstance(NodeId parentId, NodeId nodeId, OpcUaClient client) {
		try {
			List<Node> lst = client.getAddressSpace().browse(parentId).get();
			return parseNode(lst.stream().filter(x -> {
				try {
					return x.getNodeId().get().equals(nodeId);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				return false;
			}).findAny().orElse(null));
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
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

	/**
	 * retrieves the child nodes of <i>parent</i>
	 * 
	 * @param parent
	 *            the parent which children should be retrieved
	 * @param client
	 *            the OpcUaClient which is holding the connection open
	 * @param recursive
	 *            whether the child nodes should be retrieved recursively
	 * 
	 * 
	 * @return <i>parent</i> with the associated children linked to it
	 */
	private CachedBaseNode retrieveNodes(final CachedBaseNode parent, OpcUaClient client, final boolean recursive) {

		try {
			// TODO: retrieve root, if necessary

			List<Node> lst = client.getAddressSpace().browse(parent.getNodeId()).get();

			if (NodeNavigator.getInstance().isInTypesFolder(parent)) {
				List<CachedBaseNode> lstRef = browseReferencesRecursive(parent, client, recursive);
				lstRef.forEach(n -> {
					// TODO fix references of XML Schema and Binary Schema nodes
					// when deduplicating
					addChildToNode(parent, n);
				});

				List<QualifiedName> names = lstRef.stream().map(CachedBaseNode::getBrowseName)
						.collect(Collectors.toList());

				// deduplicating
				lst = lst.stream().filter(n -> {
					try {
						return !names.contains(n.getBrowseName().get());
					} catch (InterruptedException | ExecutionException e1) {
						e1.printStackTrace();
					}
					return true;
				}).collect(Collectors.toList());
			}

			lst.forEach(node -> {

				try {
					// cache node
					CachedBaseNode cn = parseNode(node);

					// set parent
					cn.setParent(parent);

					// references

					// if in type folder, retrieve all references recursively
					// and add them as children
					if (NodeNavigator.getInstance().isInTypesFolder(cn) && recursive) {
						browseReferencesRecursive(cn, client, recursive).forEach(ref -> addChildToNode(cn, ref));
					}
					cn.setReferences(browseAllReferences(cn, client));

					// retrieve children
					if (recursive) {
						retrieveNodes(cn, client, recursive);
					}

					// add child to parent
					addChildToNode(parent, cn);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}

			});
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing nodeId={} failed: {}", parent.getNodeId(), e.getMessage(), e);
		}

		return parent;
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
