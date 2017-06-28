package me.steffenjacobs.opcuadisplay.opcInterface.xml;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Test;
import org.xml.sax.SAXException;

import me.steffenjacobs.opcuadisplay.management.node.NodeGenerator;
import me.steffenjacobs.opcuadisplay.management.node.NodeNavigator;
import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;

public class TestXml {
	
	@Test
	public void testImport(){
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("base.xml");
		CachedBaseNode loaded = XmlImport.getInstance().parseFile(is, false, false, false);

		// check, if base folder structure is in place
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Objects", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Views", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/ObjectTypes", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/VariableTypes", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/DataTypes", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/ReferenceTypes", loaded));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/EventTypes", loaded));
	}

	@Test
	public void testExport() throws SAXException, IOException {
		NodeGenerator.getInstance().generateBaseTypes();

		long now = System.currentTimeMillis();
		String filename = "testXml_" + now + ".xml";
		XmlExport.getInstance().writeToFile(filename, NodeNavigator.getInstance().getRoot(), false, false);

		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory
				.newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream("UANodeSet.xsd")));
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(Files.newInputStream(Paths.get(filename), StandardOpenOption.READ)));
		
		Files.delete(Paths.get(filename));
	}

}
