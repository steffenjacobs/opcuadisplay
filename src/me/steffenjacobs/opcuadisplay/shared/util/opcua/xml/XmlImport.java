package me.steffenjacobs.opcuadisplay.shared.util.opcua.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaRuntimeException;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

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
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAInstance;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAMethod;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UANode;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UANodeSet;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAObject;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAObjectType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAReferenceType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAVariable;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAVariableType;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAView;

public class XmlImport {

	private AliasTable aliases;
	
	private static XmlImport instance;

	private XmlImport() {
		// singleton
	}

	public static XmlImport getInstance() {
		if (instance == null) {
			instance = new XmlImport();
		}
		return instance;
	}
	
	public CachedBaseNode parseFile(String xmlFile) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(UANodeSet.class);

			Unmarshaller um = context.createUnmarshaller();
			UANodeSet nodeSet = (UANodeSet) um.unmarshal(new FileReader(xmlFile));

			this.aliases = nodeSet.getAliases();
			List<UANode> nodes = nodeSet.getUAObjectOrUAVariableOrUAMethod();

			CachedBaseNode objectFolder = buildObjectTree(nodes);

			return objectFolder;
		} catch (JAXBException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	private CachedBaseNode buildObjectTree(List<UANode> nodes) {

		List<CachedBaseNode> linkedNodes = new CopyOnWriteArrayList<>();
		CachedObjectNode objectBase = new CachedObjectNode(Identifiers.ObjectsFolder);
		objectBase.setBrowseName(new QualifiedName(0, "Objects"));
		objectBase.setDescription(new LocalizedText("en",
				"The browse entry point when looking for objects in the server address space."));
		objectBase.setDisplayName(new LocalizedText("en", "Objects"));
		objectBase.setEventNotifier(UByte.valueOf(0));
		objectBase.setUserWriteMask(UInteger.valueOf(0));
		objectBase.setWriteMask(UInteger.valueOf(0));

		linkedNodes.add(objectBase);

		boolean changed = true;
		while (changed) {
			changed = false;
			Iterator<UANode> it = nodes.iterator();
			while (it.hasNext()) {
				UANode node = it.next();

				NodeId parent;
				if (node instanceof UAInstance) {
					try{
					parent = NodeId.parse(((UAInstance) node).getParentNodeId());
					}
					catch(NullPointerException ne){
						//no parent
						continue;
					}
				} else if (node instanceof UAType) {
					// TODO
					continue;
				}

				else {
					System.out.println(node.getClass().getName());
					continue;
				}
				for (CachedBaseNode cbn : linkedNodes) {
					// TODO: check, if this works with namespaces...
					if (cbn.getNodeId().equals(parent)) {
						CachedBaseNode newNode = parseNode(0, node);
						cbn.addChild(newNode);
						linkedNodes.add(newNode);
						it.remove();
						changed = true;
						break;
					}
				}

			}
		}

		return objectBase;
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
		cbn.setReferences(parseReferences(uaNode.getReferences()));

		return cbn;
	}

	private NodeId parseNodeId(int namespaceIndex, String str) {
		try {
			return NodeId.parse("ns=" + namespaceIndex + ";" + str);
		} catch (UaRuntimeException e) {
			// node.getDataType() was an alias
			return NodeId.parse("ns=" + namespaceIndex + ";" + aliases.getAlias().stream()
					.filter(a -> str.equals(a.getAlias())).map(NodeIdAlias::getValue).findFirst().orElse(null));
		}
	}

	private List<CachedReference> parseReferences(ListOfReferences refs) {
		List<CachedReference> list = new ArrayList<>();
		for (Reference ref : refs.getReference()) {
			CachedReference cr = new CachedReference(ref.getReferenceType(), new QualifiedName(0,"null-name"), "null", NodeId.parse(ref.getValue()));
			list.add(cr);
		}
		return list;
	}

	private LocalizedText parseLocalizedText(me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText lt) {
		return new LocalizedText(lt.getLocale(), lt.getValue());
	}
}
