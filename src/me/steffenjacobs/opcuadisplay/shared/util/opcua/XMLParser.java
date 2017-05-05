package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UANode;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UANodeSet;
import me.steffenjacobs.opcuadisplay.shared.domain.generated.UAObject;

public class XMLParser {

	public static void main(String[] args) {
		XMLParser.getInstance().parseFile("C:/Users/sjacobs/Desktop/dump.xml");
	}

	private static XMLParser instance;

	private XMLParser() {
		// singleton
	}

	public static XMLParser getInstance() {
		if (instance == null) {
			instance = new XMLParser();
		}
		return instance;
	}
	
	private LocalizedText parseLocalizedText(me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText lt){
		return new LocalizedText(lt.getLocale(), lt.getValue());
	}

	private CachedBaseNode parseNode(int namespaceIndex, UANode uaNode) {
		if (uaNode instanceof UAObject) {
			UAObject node = (UAObject) uaNode;
			NodeId nodeId = NodeId.parse("ns=" + namespaceIndex + ", " + node.getNodeId());
			CachedBaseNode cbn = new CachedBaseNode(nodeId, NodeClass.Object);
			
			//set browse name
			cbn.setBrowseName(new QualifiedName(namespaceIndex, node.getBrowseName()));
			
			//set description
			List<me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText> list = node.getDescription();
			if (list.size() > 0) {
				cbn.setDescription(parseLocalizedText(list.get(0)));
			}
			
			//set display name
			list = node.getDisplayName();
			if (list.size() > 0) {
				cbn.setDescription(parseLocalizedText(list.get(0)));
			}
						
			//user write mask
			cbn.setUserWriteMask(UInteger.valueOf(node.getUserWriteMask()));
			
			//write mask
			cbn.setWriteMask(UInteger.valueOf(node.getWriteMask()));
			
			//TODO: set references
			return cbn;
		}
		return null;
	}

	private CachedBaseNode buildObjectTree(List<UANode> nodes) {

		List<CachedBaseNode> linkedNodes = new ArrayList<>();
		CachedBaseNode objectBase = new CachedBaseNode(Identifiers.ObjectNode, NodeClass.Object);
		linkedNodes.add(objectBase);

		boolean unchanged = true;
		while (unchanged) {
			unchanged = false;
			Iterator<UANode> it = nodes.iterator();
			while (it.hasNext()) {
				UANode node = it.next();
				if (node instanceof UAObject) {
					UAObject obj = (UAObject) node;
					NodeId parent = NodeId.parse(obj.getParentNodeId());
					for (CachedBaseNode cbn : linkedNodes) {
						// TODO: check, if this works with namespaces...
						if (cbn.getNodeId().equals(parent)) {
							CachedBaseNode newNode = parseNode(0, node);
							cbn.addChild(newNode);
							linkedNodes.add(newNode);
							it.remove();
							unchanged = true;
						}
					}
				}

			}
		}

		return objectBase;
	}

	public CachedBaseNode parseFile(String xmlFile) {
		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(UANodeSet.class);

			Unmarshaller um = context.createUnmarshaller();
			UANodeSet nodeSet = (UANodeSet) um.unmarshal(new FileReader(xmlFile));

			List<UANode> nodes = nodeSet.getUAObjectOrUAVariableOrUAMethod();

			CachedBaseNode nd = buildObjectTree(nodes);

			System.out.println();

		} catch (JAXBException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void writeToFile(String xmlFile, CachedBaseNode cbn) {

		UANodeSet nodeSet = new UANodeSet();

		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(UANodeSet.class);
			Marshaller m;
			m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out
			m.marshal(nodeSet, System.out);

			// Write to File
			m.marshal(nodeSet, new File(xmlFile));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
