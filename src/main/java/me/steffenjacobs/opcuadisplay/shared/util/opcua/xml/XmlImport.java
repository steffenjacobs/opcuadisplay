package me.steffenjacobs.opcuadisplay.shared.util.opcua.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import me.steffenjacobs.opcuadisplay.Activator;
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
import me.steffenjacobs.opcuadisplay.shared.domain.generated.AliasTable;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.ListOfReferences;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.NodeIdAlias;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.Reference;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UADataType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAMethod;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UANode;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UANodeSet;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAObject;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAObjectType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAReferenceType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAVariable;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAVariableType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAView;
import me.steffenjacobs.opcuadisplay.shared.util.Tuple2;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.shared.util.opcua.NodeNavigator.NodeManipulator;

public class XmlImport {

	private static XmlImport instance;

	private AliasTable aliases;
	private final ArrayList<Tuple2<Reference, CachedReference>> referencesForStep2 = new ArrayList<>();

	private final List<Tuple2<CachedReference, CachedReference>> referencesForStep3 = new ArrayList<>();

	private final HashMap<NodeId, CachedBaseNode> loadedNodes = new HashMap<>();

	private XmlImport() {
		// singleton
	}

	public static XmlImport getInstance() {
		if (instance == null) {
			instance = new XmlImport();
		}
		return instance;
	}

	public CachedObjectNode parseFile(Reader xmlReader, boolean baseDataTypesImplicit) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(UANodeSet.class);

			Unmarshaller um = context.createUnmarshaller();
			UANodeSet nodeSet = (UANodeSet) um.unmarshal(xmlReader);

			this.aliases = nodeSet.getAliases();
			CopyOnWriteArrayList<UANode> nodes = new CopyOnWriteArrayList<>();
			nodes.addAll(nodeSet.getUAObjectOrUAVariableOrUAMethod());

			CachedObjectNode rootFolder = buildFullTree(nodes, baseDataTypesImplicit);

			return rootFolder;
		} catch (JAXBException e) {
			Activator.openMessageBox("Error importing XML", e.getLocalizedMessage());
			e.printStackTrace();
		}

		return null;
	}

	public CachedObjectNode parseFile(String xmlFile, boolean baseDataTypesImplicit) {
		try {
			return parseFile(new FileReader(xmlFile), baseDataTypesImplicit);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public CachedObjectNode parseFile(InputStream is, boolean baseDataTypesImplicit) {
		return parseFile(new InputStreamReader(is), baseDataTypesImplicit);
	}

	private CachedObjectNode buildFullTree(List<UANode> nodes, boolean baseDataTypesImplicit) {
		CachedObjectNode root;
		if (baseDataTypesImplicit) {
			NodeGenerator.getInstance().generateBaseTypes();
			root = NodeNavigator.getInstance().getRoot();
			NodeNavigator.getInstance().iterateNodes(root, new NodeManipulator() {

				@Override
				public void manipulate(CachedBaseNode cbn) {
					loadedNodes.put(cbn.getNodeId(), cbn);
				}
			});
		} else {
			root = NodeGenerator.getInstance().generateRoot();
			loadedNodes.put(root.getNodeId(), root);
		}

		buildReferenceBased(root, nodes);
		parseReferencesStep2();
		parseReferencesStep3();

		loadedNodes.forEach((x, y) -> System.out.println(y.getBrowseName() + " - " + y.getNodeId()));

		return root;
	}

//	private CachedBaseNode buildObjectTree(List<UANode> nodes) {

		// // create object node
		// CachedObjectNode objectBase = new
		// CachedObjectNode(Identifiers.ObjectsFolder);
		// objectBase.setBrowseName(new QualifiedName(0, "Objects"));
		// objectBase.setDescription(new LocalizedText("en",
		// "The browse entry point when looking for objects in the server
		// address space."));
		// objectBase.setDisplayName(new LocalizedText("en", "Objects"));
		// objectBase.setEventNotifier(UByte.valueOf(0));
		// objectBase.setUserWriteMask(UInteger.valueOf(0));
		// objectBase.setWriteMask(UInteger.valueOf(0));
//		return buildReferenceBased(NodeNavigator.getInstance().navigateByName("Root/Objects"), nodes);
//	}

	/**
	 * build the tree based on the references of the root node. Nodes are
	 * resolved via the refNode of the references recursively.<br/>
	 * CachedReferences are added, but not BrowseName and TypeDefinition of
	 * CachedReferences can only be set, after this process has finished.
	 * 
	 * @param root
	 *            the start node
	 * @param nodes
	 *            a list of the loaded nodes that should be mapped to a tree
	 *            structure
	 * @return <i>root</i>
	 */
	private CachedBaseNode buildReferenceBased(CachedBaseNode root, List<UANode> nodes) {
		Iterator<CachedReference> it = root.getReferences().iterator();
		while (it.hasNext()) {
			CachedReference cr = it.next();
			if (NodeNavigator.getInstance().isHierarchicalReference(cr.getReferenceType())) {
				UANode child = findByNodeId(nodes, cr.getRefNodeId());
				CachedBaseNode childNode;
				if (child == null) {
					System.out.println("Error: Node not found: " + cr.getReferenceType() + " - " + cr.getRefNodeId());
					childNode = loadedNodes.get(cr.getRefNodeId());

					if (childNode == null) {
						System.out.println("really hard for " + cr.getReferenceType() + " - " + cr.getRefNodeId());
						continue;
					}
				}

				else {
					childNode = parseNode(0, child);
				}

				childNode = NodeGenerator.getInstance().mergeInsertNode(childNode, root);
				loadedNodes.put(childNode.getNodeId(), childNode);

				nodes.remove(child);

				buildReferenceBased(childNode, nodes);
			}
		}		
		
		return root;

	}

	/**
	 * @return the UANode from the <i>list</i>, associated with the given
	 *         <i>nodeId</i>
	 */
	private UANode findByNodeId(List<UANode> list, NodeId nodeId) {
		return list.stream().filter(x -> parseNodeId(0, x.getNodeId()).equals(nodeId)).findAny().orElse(null);
	}

	private CachedBaseNode parseNode(int namespaceIndex, UANode uaNode) {

		CachedBaseNode cbn = null;

		if (uaNode instanceof UAObject) {
			UAObject node = (UAObject) uaNode;
			NodeId nodeId = NodeId.parse("ns=" + namespaceIndex + ";" + node.getNodeId());
			cbn = new CachedObjectNode(nodeId);

			// event notifier
			((CachedObjectNode) cbn).setEventNotifier(UByte.valueOf(node.getEventNotifier()));
		}

		else if (uaNode instanceof UAVariable) {
			UAVariable node = (UAVariable) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedVariableNode cvn = new CachedVariableNode(nodeId);

			cvn.setValue(node.getValue().getAny());

			cvn.setDataType(parseNodeId(namespaceIndex, node.getDataType()));

			cvn.setValueRank(node.getValueRank());

			UInteger[] arr = node.getArrayDimensions().stream().map(x -> UInteger.valueOf(x)).toArray(UInteger[]::new);
			cvn.setArrayDimensions(arr);

			cvn.setAccessLevel(UByte.valueOf(node.getAccessLevel()));
			cvn.setUserAccessLevel(UByte.valueOf(node.getUserAccessLevel()));
			cvn.setMinimumSamplingInterval(node.getMinimumSamplingInterval());
			cvn.setHistorizing(node.isHistorizing());

			cbn = cvn;
		}

		else if (uaNode instanceof UAMethod) {
			UAMethod node = (UAMethod) uaNode;

			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedMethodNode cmn = new CachedMethodNode(nodeId);

			cmn.setExecutable(node.isExecutable());
			cmn.setUserExecutable(node.isUserExecutable());

			cbn = cmn;
		}

		else if (uaNode instanceof UAView) {
			UAView node = (UAView) uaNode;

			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedViewNode cvn = new CachedViewNode(nodeId);

			cvn.setContainsNoLoop(node.isContainsNoLoops());
			cvn.setEventNotifier(UByte.valueOf(node.getEventNotifier()));

			cbn = cvn;
		}

		else if (uaNode instanceof UAObjectType) {
			UAObjectType node = (UAObjectType) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedObjectTypeNode con = new CachedObjectTypeNode(nodeId);

			con.setAbstract(node.isIsAbstract());

			cbn = con;
		}

		else if (uaNode instanceof UADataType) {
			UADataType node = (UADataType) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedDataTypeNode cdn = new CachedDataTypeNode(nodeId);
			cdn.setAbstract(node.isIsAbstract());
			cbn = cdn;
		}

		else if (uaNode instanceof UAVariableType) {
			UAVariableType node = (UAVariableType) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedVariableTypeNode cvn = new CachedVariableTypeNode(nodeId);
			cvn.setValue(node.getValue());
			cvn.setDataType(parseNodeId(namespaceIndex, node.getDataType()));
			cvn.setValueRank(node.getValueRank());
			UInteger[] arr = node.getArrayDimensions().stream().map(x -> UInteger.valueOf(x)).toArray(UInteger[]::new);
			cvn.setArrayDimensions(arr);
			cvn.setAbstract(node.isIsAbstract());
			cbn = cvn;
		}

		else if (uaNode instanceof UAReferenceType) {
			UAReferenceType node = (UAReferenceType) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedReferenceTypeNode crt = new CachedReferenceTypeNode(nodeId);

			crt.setAbstract(node.isIsAbstract());
			crt.setSymmetric(node.isSymmetric());
			crt.setInverseName(parseLocalizedText(node.getInverseName().get(0)));

			cbn = crt;
			;
		}

		// set browse name
		cbn.setBrowseName(new QualifiedName(namespaceIndex, uaNode.getBrowseName()));

		// set description
		List<me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText> list = uaNode.getDescription();
		if (list.size() > 0) {
			cbn.setDescription(parseLocalizedText(list.get(0)));
		}

		// set display name
		list = uaNode.getDisplayName();
		if (list.size() > 0) {
			cbn.setDisplayName(parseLocalizedText(list.get(0)));
		} else {
			cbn.setDisplayName(new LocalizedText("", cbn.getBrowseName().getName()));
		}

		// user write mask
		cbn.setUserWriteMask(UInteger.valueOf(uaNode.getUserWriteMask()));

		// write mask
		cbn.setWriteMask(UInteger.valueOf(uaNode.getWriteMask()));

		// references
		cbn.setReferences(parseReferencesStep1(uaNode.getReferences()));

		return cbn;
	}

	/**
	 * @return the NodeId object from the namespaceIndex and a String <i>str
	 *         (i=[0-9]*)</i>
	 */
	private NodeId parseNodeId(int namespaceIndex, String str) {
		try {
			return NodeId.parse("ns=" + namespaceIndex + ";" + str);
		} catch (UaRuntimeException e) {
			// node.getDataType() was an alias
			return NodeId.parse("ns=" + namespaceIndex + ";" + aliases.getAlias().stream()
					.filter(a -> str.equals(a.getAlias())).map(NodeIdAlias::getValue).findFirst().orElse(null));
		}
	}

	/**
	 * ReferenceType and ReferredNodeId are set here, while the BrowseName and
	 * the TypeDefinition are set in steps 2 and 3.
	 * 
	 * @return a list of CachedReferences from <i>ref</i>. *
	 */
	private List<CachedReference> parseReferencesStep1(ListOfReferences refs) {
		List<CachedReference> list = new CopyOnWriteArrayList<CachedReference>();
		for (Reference ref : refs.getReference()) {
			CachedReference cr = new CachedReference(ref.getReferenceType(), new QualifiedName(0, "null-name"), "null",
					NodeId.parse(ref.getValue()));
			list.add(cr);
			referencesForStep2.add(new Tuple2<Reference, CachedReference>(ref, cr));
		}
		return list;
	}

	/** set BrowseName, which is the browse name of the associated child node */
	private void parseReferencesStep2() {
		referencesForStep2.forEach(t -> {

			NodeId nid = parseNodeId(0, t.getX().getValue());
			CachedBaseNode refNode = loadedNodes.get(nid);
			if (refNode != null) {
				t.getY().setBrowseName(refNode.getBrowseName());

				NodeNavigator.getInstance().getTypeDefinition(refNode).ifPresent(
						cr -> referencesForStep3.add(new Tuple2<CachedReference, CachedReference>(t.getY(), cr)));
			}
		});
	}

	/**
	 * set TypeDefinition, which is the BrowseName of the reference
	 * HasTypeDefinition of the associated child node
	 */
	private void parseReferencesStep3() {
		referencesForStep3.forEach(t -> {

			t.getX().setTypeDefinition(t.getY().getBrowseName().getName());
		});
	}

	private LocalizedText parseLocalizedText(me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText lt) {
		return new LocalizedText(lt.getLocale(), lt.getValue());
	}
}
