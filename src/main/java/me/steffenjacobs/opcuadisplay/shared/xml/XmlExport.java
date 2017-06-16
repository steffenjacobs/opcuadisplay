package me.steffenjacobs.opcuadisplay.shared.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

import com.google.common.collect.Lists;

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
import me.steffenjacobs.opcuadisplay.shared.domain.generated.ListOfReferences;
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
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeNavigator.NodeManipulator;
/** @author Steffen Jacobs */
public class XmlExport {

	public static void main(String[] args) {
		CachedBaseNode cbn = XmlImport.getInstance().parseFile("C:/Users/sjacobs/Desktop/dump.xml", false);
		XmlExport.getInstance().writeToFile("C:/Users/sjacobs/Desktop/dump.xml2", cbn);
	}

	private static XmlExport instance;

	private XmlExport() {
		// singleton
	}

	public static XmlExport getInstance() {
		if (instance == null) {
			instance = new XmlExport();
		}
		return instance;
	}

	public void writeToFile(String xmlFile, CachedBaseNode cbn) {

		UANodeSet nodeSet = new UANodeSet();

		nodeSet.getUAObjectOrUAVariableOrUAMethod().addAll(parseObjectTree(cbn));

		try {
			// create JAXB context and instantiate marshaller
			JAXBContext context = JAXBContext.newInstance(UANodeSet.class);
			Marshaller m;
			m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to File
			m.marshal(nodeSet, new File(xmlFile));
		} catch (JAXBException e) {
			Activator.openMessageBox("Error exporting XML", "" + e.getLinkedException().getMessage());
			e.printStackTrace();
		}
	}

	private Object convertValue(Object obj) {
		// if(obj instanceof String[]){
		// List<String> list = new ArrayList<>();
		// for(String s : (String[])obj){
		// list.add(s);
		// }
		// ListOfStrings result = new ListOfStrings();
		// result.getString().addAll(list);
		// return result;
		// }
		if (obj != null) {
			JAXBElement<String> jaxbElement = new JAXBElement<String>(new QName("root-element"), String.class,
					obj.toString());
			return jaxbElement;
		}
		return obj;
	}

	private UANode parseNode(CachedBaseNode cbn) {
		UANode uaNode = null;

		if (cbn instanceof CachedReferenceTypeNode) {
			CachedReferenceTypeNode node = (CachedReferenceTypeNode) cbn;
			UAReferenceType urt = new UAReferenceType();

			urt.setIsAbstract(node.isAbstract());
			urt.setSymmetric(node.isSymmetric());

			urt.getInverseName().add(parseLocalizedText(node.getInverseName()));

			uaNode = urt;
		}

		if (cbn instanceof CachedObjectNode) {
			CachedObjectNode node = (CachedObjectNode) cbn;
			UAObject uao = new UAObject();

			// event notifier
			uao.setEventNotifier(node.getEventNotifier() != null ? node.getEventNotifier().shortValue() : 0);

			uao.setParentNodeId(node.getParent() != null && node.getParent().getNodeId() != null
					? parseNodeId(node.getParent().getNodeId()) : null);

			uaNode = uao;
		}

		else if (cbn instanceof CachedVariableNode) {
			CachedVariableNode node = (CachedVariableNode) cbn;
			UAVariable uav = new UAVariable();

			UAVariable.Value val = new UAVariable.Value();
			val.setAny(convertValue(node.getValue()));
			uav.setValue(val);

			uav.setDataType(parseNodeId(node.getDataType()));
			uav.setValueRank(node.getValueRank());

			uav.getArrayDimensions().addAll(Lists.newArrayList(node.getArrayDimensions()).stream()
					.map(x -> x.toString()).collect(Collectors.toList()));

			uav.setAccessLevel(node.getAccessLevel().shortValue());
			uav.setUserAccessLevel(node.getUserAccessLevel().shortValue());
			uav.setMinimumSamplingInterval(node.getMinimumSamplingInterval());
			uav.setHistorizing(node.isHistorizing());

			uav.setParentNodeId(node.getParent() != null && node.getParent().getNodeId() != null
					? parseNodeId(node.getParent().getNodeId()) : null);

			uaNode = uav;
		}

		else if (cbn instanceof CachedMethodNode) {
			CachedMethodNode node = (CachedMethodNode) cbn;
			UAMethod uam = new UAMethod();

			uam.setExecutable(node.isExecutable());
			uam.setUserExecutable(node.isUserExecutable());

			uam.setParentNodeId(node.getParent() != null && node.getParent().getNodeId() != null
					? parseNodeId(node.getParent().getNodeId()) : null);

			uaNode = uam;
		}

		else if (cbn instanceof CachedViewNode) {
			CachedViewNode node = (CachedViewNode) cbn;
			UAView uav = new UAView();

			uav.setContainsNoLoops(node.isContainsNoLoop());
			uav.setEventNotifier(node.getEventNotifier().shortValue());

			uaNode = uav;
		}

		else if (cbn instanceof CachedObjectTypeNode) {
			CachedObjectTypeNode node = (CachedObjectTypeNode) cbn;
			UAObjectType uao = new UAObjectType();
			uao.setIsAbstract(node.isAbstract());

			uaNode = uao;
		}

		else if (cbn instanceof CachedDataTypeNode) {
			CachedDataTypeNode node = (CachedDataTypeNode) cbn;
			UADataType uad = new UADataType();
			uad.setIsAbstract(node.isAbstract());

			uaNode = uad;
		}

		else if (cbn instanceof CachedVariableTypeNode) {
			CachedVariableTypeNode node = (CachedVariableTypeNode) cbn;
			UAVariableType uav = new UAVariableType();

			UAVariableType.Value val = new UAVariableType.Value();
			val.setAny(convertValue(node.getValue()));
			uav.setValue(val);

			uav.setDataType(parseNodeId(node.getDataType()));
			uav.setValueRank(node.getValueRank());

			uav.getArrayDimensions().addAll(Lists.newArrayList(node.getArrayDimensions()).stream()
					.map(x -> x.toString()).collect(Collectors.toList()));

			uav.setIsAbstract(node.isAbstract());

			uaNode = uav;
		}
		// TODO reference types

		// set node id
		if (uaNode == null) {
			System.out.println(NodeNavigator.getInstance().pathAsString(cbn) + " - " + cbn.getNodeClass());
		}
		uaNode.setNodeId(parseNodeId(cbn.getNodeId()));

		// set browse name
		uaNode.setBrowseName(cbn.getBrowseName().getName());

		// set description
		uaNode.getDescription().add(parseLocalizedText(cbn.getDescription()));

		// set display name
		uaNode.getDisplayName().add(parseLocalizedText(cbn.getDisplayName()));

		// user write mask
		uaNode.setUserWriteMask(cbn.getUserWriteMask().longValue());

		// write mask
		uaNode.setWriteMask(cbn.getWriteMask().longValue());

		// references
		uaNode.setReferences(parseReferences(cbn.getReferences()));

		return uaNode;
	}

	private ListOfReferences parseReferences(List<CachedReference> references) {
		ListOfReferences result = new ListOfReferences();

		for (CachedReference ref : references) {
			Reference r = new Reference();
			r.setReferenceType(ref.getReferenceType());
			if (ref.getRefNodeId() == null) {
				System.out.println(ref.getTypeDefinition() + " - " + ref.getReferenceType());
			}
			r.setValue(parseNodeId(ref.getRefNodeId()));
			r.setIsForward(false);
			result.getReference().add(r);
		}

		return result;
	}

	private String parseNodeId(NodeId id) {
		return "i=" + id.getIdentifier().toString();
	}

	private List<UANode> parseObjectTree(CachedBaseNode cbn) {

		final List<CachedBaseNode> list = new ArrayList<>();

		NodeNavigator.getInstance().iterateNodes(cbn, new NodeManipulator() {

			@Override
			public void manipulate(CachedBaseNode cbn) {
				list.add(cbn);
			}
		});

		return list.stream().map(x -> parseNode(x)).collect(Collectors.toList());
	}

	private me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText parseLocalizedText(LocalizedText lt) {
		me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText res = new me.steffenjacobs.opcuadisplay.shared.domain.generated.LocalizedText();
		res.setLocale(lt.getLocale());
		res.setValue(lt.getText());
		return res;
	}

}
