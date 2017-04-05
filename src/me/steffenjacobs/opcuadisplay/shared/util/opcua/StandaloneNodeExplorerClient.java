package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReferenceTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedViewNode;

public class StandaloneNodeExplorerClient {

	private static final Logger logger = LoggerFactory.getLogger(StandaloneNodeExplorerClient.class);

	public static void main(String[] args) throws Exception {
		new StandaloneNodeExplorerClient().retrieveNodes("opc.tcp://localhost:12686/example");
	}

	public CachedBaseNode retrieveNodes(String url) throws Exception {

		OpcUaClient client = createClient(url);

		// synchronous connect
		client.connect().get();

		// start browsing at root folder
		CachedBaseNode root = retrieveNodes(CachedBaseNode.createNewRoot(), client, Identifiers.RootFolder);

		// disconnect
		client.disconnect();

		return root;
	}

	private boolean isInTypesFolder(CachedBaseNode node) {

		while ((node = node.getParent()) != null) {
			if (node.getBrowseName().getName().equals("Types")) {
				return true;
			}
		}

		return false;

	}

	private List<CachedBaseNode> browseReferences(CachedBaseNode node, OpcUaClient client) {
		List<CachedBaseNode> ref = new ArrayList<>();
		try {
			BrowseDescription browse = new BrowseDescription(node.getNodeId(), BrowseDirection.Forward,
					Identifiers.References, true,
					uint(NodeClass.Object.getValue() | NodeClass.DataType.getValue()
							| NodeClass.ReferenceType.getValue() | NodeClass.Method.getValue()
							| NodeClass.Variable.getValue()),

					uint(BrowseResultMask.All.getValue()));

			BrowseResult browseResult = client.browse(browse).get();

			List<ReferenceDescription> references = toList(browseResult.getReferences());

			System.out.println("received " + references.size() + " references for " + node.getBrowseName().getName());

			for (ReferenceDescription rd : references) {

				if (rd.getNodeClass() == NodeClass.DataType || rd.getNodeClass() == NodeClass.ObjectType
						|| rd.getNodeClass() == NodeClass.ReferenceType
						|| rd.getNodeClass() == NodeClass.VariableType) {

					ref.add(new CachedBaseNode(rd));
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing references for nodeId={} failed: {}", node, e.getMessage(), e);
		}

		return ref;
	}

	private List<CachedBaseNode> browseReferencesRecursive(CachedBaseNode node, OpcUaClient client) {
		List<CachedBaseNode> ref = new ArrayList<>();
		try {
			BrowseDescription browse = new BrowseDescription(node.getNodeId(), BrowseDirection.Forward,
					Identifiers.References, true,
					uint(NodeClass.DataType.getValue() | NodeClass.ReferenceType.getValue()
							| NodeClass.ObjectType.getValue() | NodeClass.VariableType.getValue()),

					uint(BrowseResultMask.All.getValue()));

			BrowseResult browseResult = client.browse(browse).get();

			List<ReferenceDescription> references = toList(browseResult.getReferences());

			System.out.println("received " + references.size() + " references for " + node.getBrowseName().getName());

			for (ReferenceDescription rd : references) {

				if (rd.getNodeClass() == NodeClass.DataType || rd.getNodeClass() == NodeClass.ObjectType
						|| rd.getNodeClass() == NodeClass.ReferenceType
						|| rd.getNodeClass() == NodeClass.VariableType) {
					CachedBaseNode cbn = retrieveTypeNode(rd.getNodeId().local().orElse(null), client);
					browseReferencesRecursive(cbn, client).forEach(nd -> cbn.addChild(nd));
					ref.add(cbn);
				}
			}
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Browsing references for nodeId={} failed: {}", node, e.getMessage(), e);
		}

		return ref;
	}

	private boolean isFolderDataType(CachedBaseNode node) {
		for (CachedBaseNode cn : node.getReferences()) {
			if (cn.getBrowseName().getName().equals("FolderType"))
				return true;
		}
		return false;
	}

	private CachedBaseNode retrieveTypeNode(NodeId nodeId, OpcUaClient client) {
		UaNode node = null;
		try {
			node = client.getAddressSpace().getNodeInstance(nodeId).get();

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
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private CachedBaseNode retrieveNodes(CachedBaseNode parent, OpcUaClient client, NodeId browseRoot) {
		try {

			// retrieve node
			List<Node> nodes = client.getAddressSpace().browse(browseRoot).get();

			for (Node node : nodes) {
				// cache node
				CachedBaseNode cn;
				if (node instanceof UaDataTypeNode) {
					cn = new CachedDataTypeNode((UaDataTypeNode) node);
				} else if (node instanceof UaMethodNode) {
					cn = new CachedMethodNode((UaMethodNode) node);
				} else if (node instanceof UaObjectNode) {
					cn = new CachedObjectNode((UaObjectNode) node);
				} else if (node instanceof UaObjectTypeNode) {
					cn = new CachedObjectTypeNode((UaObjectTypeNode) node);
				} else if (node instanceof UaReferenceTypeNode) {
					cn = new CachedReferenceTypeNode((UaReferenceTypeNode) node);
				} else if (node instanceof UaVariableNode) {
					cn = new CachedVariableNode((UaVariableNode) node);
				} else if (node instanceof UaVariableTypeNode) {
					cn = new CachedVariableTypeNode((UaVariableTypeNode) node);
				} else if (node instanceof UaViewNode) {
					cn = new CachedViewNode((UaViewNode) node);
				} else {
					cn = new CachedBaseNode(node);
				}

				// set parent
				cn.setParent(parent);

				// references
				if (isInTypesFolder(cn)) {
					browseReferencesRecursive(cn, client)
							.forEach(ref -> cn.addChild(retrieveNodes(ref, client, ref.getNodeId())));

				} else {
					cn.setReferences(browseReferences(cn, client));
				}

				// retrieve children
				retrieveNodes(cn, client, cn.getNodeId());

				if (isFolderDataType(cn)) {
					cn.setFolder(true);
				}

				// add child to parent
				if (parent == CachedBaseNode.getRoot()) {
					parent.addChild(cn);
				} else {
					parent.addChild(cn);
				}
			}
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
