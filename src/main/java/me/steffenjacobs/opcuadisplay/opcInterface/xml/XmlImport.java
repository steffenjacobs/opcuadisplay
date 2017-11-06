package me.steffenjacobs.opcuadisplay.opcInterface.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.w3c.dom.Element;

import me.steffenjacobs.opcuadisplay.Activator;
import me.steffenjacobs.opcuadisplay.management.node.NodeGenerator;
import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;
import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator.NodeManipulator;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedDataTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedMethodNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedReferenceTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedVariableNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedVariableTypeNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedViewNode;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.AliasTable;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.ListOfReferences;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.NodeIdAlias;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.Reference;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UADataType;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAMethod;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UANode;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UANodeSet;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAObject;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAObjectType;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAReferenceType;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAVariable;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAVariableType;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAVariableType.Value;
import me.steffenjacobs.opcuadisplay.management.node.domain.generated.UAView;

/** @author Steffen Jacobs */
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

	/**
	 * @param baseDataTypesImplicit
	 *            true, if the base data types are not stored in the XML file or not
	 * @param freeOpcUaModelerCompatibility
	 *            true, if the XMl file is compatible with the free opc ua modeler
	 * @return a node structure read from the <i>xmlReader</i>
	 */
	public CachedObjectNode parseFile(Reader xmlReader, boolean baseDataTypesImplicit, boolean freeOpcUaModelerCompatibility, boolean merge) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(UANodeSet.class);

			Unmarshaller um = context.createUnmarshaller();
			UANodeSet nodeSet = (UANodeSet) um.unmarshal(xmlReader);

			if (this.aliases == null) {
				this.aliases = nodeSet.getAliases();
			} else {
				if (nodeSet.getAliases() != null) {
					this.aliases.getAlias().addAll(nodeSet.getAliases().getAlias());
				}
			}
			CopyOnWriteArrayList<UANode> nodes = new CopyOnWriteArrayList<>();
			nodes.addAll(nodeSet.getUAObjectOrUAVariableOrUAMethod());

			for (UANode n : nodes) {
				if (!n.getNodeId().contains("ns=")) {
					n.setNodeId("ns=0;" + n.getNodeId());
				}
			}

			CachedObjectNode rootFolder = buildFullTree(nodes, baseDataTypesImplicit, freeOpcUaModelerCompatibility, merge);

			return rootFolder;
		} catch (JAXBException e) {
			Activator.openMessageBoxError("Error importing XML", e.getLocalizedMessage());
		}

		return null;
	}

	/**
	 * @param baseDataTypesImplicit
	 *            true, if the base data types are not stored in the XML file or not
	 * @param freeOpcUaModelerCompatibility
	 *            true, if the XMl file is compatible with the free opc ua modeler
	 * @return a node structure read from the <i>xmlReader</i>
	 */
	public CachedObjectNode parseFile(String xmlFile, boolean baseDataTypesImplicit, boolean freeOpcUaModelerCompatibility, boolean merge) {
		try {
			return parseFile(new FileReader(xmlFile), baseDataTypesImplicit, freeOpcUaModelerCompatibility, merge);
		} catch (FileNotFoundException e) {
			Activator.openMessageBoxError("Error", e.getLocalizedMessage());
		}
		return null;
	}

	/**
	 * @param baseDataTypesImplicit
	 *            true, if the base data types are not stored in the XML file or not
	 * @param freeOpcUaModelerCompatibility
	 *            true, if the XMl file is compatible with the free opc ua modeler
	 * @return a node structure read from the <i>xmlReader</i>
	 */
	public CachedObjectNode parseFile(InputStream is, boolean baseDataTypesImplicit, boolean freeOpcUaModelerCompatibility, boolean merge) {
		return parseFile(new InputStreamReader(is), baseDataTypesImplicit, freeOpcUaModelerCompatibility, merge);
	}

	/**
	 * @param baseDataTypesImplicit
	 *            true, if the base data types are not stored in the XML file or not
	 * @param freeOpcUaModelerCompatibility
	 *            true, if the XMl file is compatible with the free opc ua modeler
	 * @param merge
	 *            true, if the loaded node structure should be merged into the
	 *            existing project /** @return the node tree read from the list of
	 *            nodes <i>nodes</i>
	 */
	private CachedObjectNode buildFullTree(List<UANode> nodes, boolean baseDataTypesImplicit, boolean freeOpcUaModelerCompatibility, boolean merge) {
		CachedObjectNode root;
		if (baseDataTypesImplicit && !merge) {
			NodeGenerator.getInstance().generateBaseTypes();
			root = NodeNavigator.getInstance().getRoot();
			NodeNavigator.getInstance().iterateNodes(root, new NodeManipulator() {

				@Override
				public void manipulate(CachedBaseNode cbn) {
					loadedNodes.put(cbn.getNodeId(), cbn);
				}
			});
		} else if (!merge) {
			root = NodeGenerator.getInstance().generateRoot();
			loadedNodes.put(root.getNodeId(), root);
		} else {
			root = NodeNavigator.getInstance().getRoot();
			NodeNavigator.getInstance().iterateNodes(root, new NodeManipulator() {

				@Override
				public void manipulate(CachedBaseNode cbn) {
					loadedNodes.put(cbn.getNodeId(), cbn);
				}
			});
		}

		buildReferenceBased(root, nodes);
		parseReferencesStep2();
		parseReferencesStep3();

		// needed, if xml file from free opc ua modeler had been loaded
		if (freeOpcUaModelerCompatibility) {
			for (UANode node : nodes) {
				if (node.getBrowseName().startsWith("0:")) {
					for (Reference ref : node.getReferences().getReference()) {
						if (!ref.isIsForward()) {
							CachedBaseNode parent = loadedNodes.get(NodeId.parse(ref.getValue()));
							CachedBaseNode n = parseNode(0, node);
							NodeGenerator.getInstance().insertNode(n, parent, true);
							loadedNodes.put(n.getNodeId(), n);
						}
					}
				}
			}
		}

		return root;
	}

	/**
	 * build the tree based on the references of the root node. Nodes are resolved
	 * via the refNode of the references recursively.<br/>
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

	/** @return the CachedBaseNode from the UANode <i>uaNode</i> */
	private CachedBaseNode parseNode(int namespaceIndex, UANode uaNode) {

		CachedBaseNode cbn = null;

		if (uaNode instanceof UAObject) {
			UAObject node = (UAObject) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			cbn = new CachedObjectNode(nodeId);

			// event notifier
			((CachedObjectNode) cbn).setEventNotifier(UByte.valueOf(node.getEventNotifier()));
		}

		else if (uaNode instanceof UAVariable) {
			UAVariable node = (UAVariable) uaNode;
			NodeId nodeId = parseNodeId(namespaceIndex, node.getNodeId());
			CachedVariableNode cvn = new CachedVariableNode(nodeId);

			cvn.setDataType(parseNodeId(namespaceIndex, node.getDataType()));

			cvn.setValue(convertValue(node.getValue(), cvn.getDataType()));

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
			cvn.setDataType(parseNodeId(namespaceIndex, node.getDataType()));
			cvn.setValue(convertValue(node.getValue(), cvn.getDataType()));
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
		cbn.setBrowseName(QualifiedName.parse(uaNode.getBrowseName()));

		// set description
		List<me.steffenjacobs.opcuadisplay.management.node.domain.generated.LocalizedText> list = uaNode.getDescription();
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
	 * converts tag to a value set
	 * 
	 * @return the value inside the tag <i>val</i>
	 */
	private Object convertValue(Object val, NodeId datatype) {
		Object dat = null;

		if (val instanceof Value) {
			Value v = (Value) val;
			dat = v.getAny();
		} else if (val instanceof UAVariable.Value) {
			UAVariable.Value v = (UAVariable.Value) val;
			dat = v.getAny();
		}
		if (dat == null) {
			return null;
		}

		String type = null;
		String value = null;

		if (dat instanceof Element) {
			Element elem = (Element) dat;
			type = elem.getLocalName();
			value = elem.getFirstChild().getTextContent();
		}

		if (type.equals("root-element") || type == null) {
			return null;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd hh/mm/ss/SSS Z");

		if (type != null) {
			switch (type) {
			case "Boolean":
				return Boolean.parseBoolean(value);
			case "SByte":
				return Byte.parseByte(value);
			case "Byte":
				return UInteger.valueOf(value);
			case "Int16":
				return Integer.parseInt(value);
			case "UInt16":
				return UInteger.valueOf(value);
			case "Int32":
				return Integer.parseInt(value);
			case "UInt32":
				return UInteger.valueOf(value);
			case "Int64":
				return Long.parseLong(value);
			case "UInt64":
				return UInteger.valueOf(value);
			case "Float":
				return Float.parseFloat(value);
			case "Double":
				return Double.parseDouble(value);
			case "String":
				return value;
			case "DateTime":
				try {
					return new DateTime(sdf.parse(value));
				} catch (ParseException e) {
					return null;
				}
			}
		}
		return null;
	}

	/**
	 * @return the NodeId object from the namespaceIndex and a String <i>str
	 *         (i=[0-9]*)</i>
	 */
	private NodeId parseNodeId(int namespaceIndex, String str) {
		try {
			if (str.contains("ns=")) {
				return NodeId.parse(str);
			} else {
				return NodeId.parse("ns=" + namespaceIndex + ";" + str);
			}
		} catch (UaRuntimeException e) {
			// node.getDataType() was an alias
			return NodeId.parse(aliases.getAlias().stream().filter(a -> str.equals(a.getAlias())).map(NodeIdAlias::getValue).findFirst().orElse(null));
		}
	}

	/**
	 * ReferenceType and ReferredNodeId are set here, while the BrowseName and the
	 * TypeDefinition are set in steps 2 and 3.
	 * 
	 * @return a list of CachedReferences from <i>ref</i>. *
	 */
	private List<CachedReference> parseReferencesStep1(ListOfReferences refs) {
		List<CachedReference> list = new CopyOnWriteArrayList<CachedReference>();
		for (Reference ref : refs.getReference()) {
			if (ref.isIsForward()) {
				CachedReference cr = new CachedReference(ref.getReferenceType(), new QualifiedName(0, "null-name"), "null", NodeId.parse(ref.getValue()));
				list.add(cr);
				referencesForStep2.add(new Tuple2<Reference, CachedReference>(ref, cr));
			}
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

				NodeNavigator.getInstance().getTypeDefinition(refNode).ifPresent(cr -> referencesForStep3.add(new Tuple2<CachedReference, CachedReference>(t.getY(), cr)));
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

	/** converts the LocalizedText */
	private LocalizedText parseLocalizedText(me.steffenjacobs.opcuadisplay.management.node.domain.generated.LocalizedText lt) {
		return new LocalizedText(lt.getLocale(), lt.getValue());
	}
}
