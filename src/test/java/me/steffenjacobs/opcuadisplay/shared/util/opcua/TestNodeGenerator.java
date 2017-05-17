package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.junit.Test;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedReference;
import me.steffenjacobs.opcuadisplay.views.explorer.dialogs.DialogFactory.AddDialogType;

public class TestNodeGenerator {

	/** tests, if NodeGenerator is a singleton */
	@Test
	public void testSingleton() {
		assertSame(NodeGenerator.getInstance(), NodeGenerator.getInstance());
	}

	/** tests, if NodeGenerator.generateRoot() generates a correct root */
	@Test
	public void testRootGeneration() {
		CachedObjectNode root = NodeGenerator.getInstance().generateRoot();

		// check, if root had been set correctly
		assertSame(root, NodeNavigator.getInstance().getRoot());

		// check nodeId
		assertEquals(Identifiers.RootFolder, root.getNodeId());

		// check name
		assertEquals("Root", root.getBrowseName().getName());

		// check references
		CachedReference f = new CachedReference("HasTypeDefinition", new QualifiedName(0, "FolderType"), "null",
				Identifiers.FolderType);
		CachedReference oo = new CachedReference("Organizes", new QualifiedName(0, "Objects"), "FolderType",
				Identifiers.ObjectsFolder);
		CachedReference ot = new CachedReference("Organizes", new QualifiedName(0, "Types"), "FolderType",
				Identifiers.TypesFolder);
		CachedReference ov = new CachedReference("Organizes", new QualifiedName(0, "Views"), "FolderType",
				Identifiers.ViewsFolder);

		List<CachedReference> expectedRefs = Lists.newArrayList(f, oo, ot, ov);

		// check completeness & minimality
		assertEquals(expectedRefs.size(), root.getReferences().size());
		expectedRefs.forEach(r -> assertTrue(root.getReferences().contains(r)));
	}

	/**
	 * check, if NodeGenerator.generateFolders() generates the correct folders
	 */
	@Test
	public void testGenerateFolders() {
		NodeGenerator.getInstance().generateFolders();
		NodeNavigator.getInstance().getRoot();

		// check, if folders exist as planned
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Objects"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Views"));

		assertEquals(3, NodeNavigator.getInstance().getRoot().getChildren().length);
	}

	/**
	 * test, if NodeGenerator.generateBaseTypes() generates the correct base
	 * types with random selected nodes
	 */
	@Test
	public void testBaseTypes() {
		NodeGenerator.getInstance().generateBaseTypes();
		NodeNavigator.getInstance().getRoot();

		// check, if base folder structure is in place
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Objects"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Views"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/ObjectTypes"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/VariableTypes"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/DataTypes"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/ReferenceTypes"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/EventTypes"));

		// check specific nodes
		assertNotNull(NodeNavigator.getInstance()
				.navigateByName("Root/Types/ObjectTypes/BaseObjectType/FolderType/FileDirectoryType/<FileName>"));
		assertNotNull(NodeNavigator.getInstance().navigateByName(
				"Root/Types/VariableTypes/BaseVariableType/BaseDataVariableType/DataItemType/AnalogItemType/EURange"));
		assertNotNull(
				NodeNavigator.getInstance().navigateByName("Root/Types/DataTypes/BaseDataType/Number/UInteger/Byte"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/DataTypes/XML Schema/Opc.Ua/Argument"));
		assertNotNull(NodeNavigator.getInstance().navigateByName("Root/Types/DataTypes/OPC Binary/Opc.Ua/Argument"));
		assertNotNull(NodeNavigator.getInstance().navigateByName(
				"Root/Types/EventTypes/BaseEventType/AuditEventType/AuditSecurityEventType/AuditChannelEventType/AuditOpenSecureChannelEventType/ClientCertificate"));
	}

	/** test, if NodeGenerator.insertNode(node) inserts a node correctly */
	@Test
	public void testInsertNode() {
		NodeGenerator.getInstance().generateBaseTypes();

		// create test node to insert
		CachedObjectTypeNode testObjectType = new CachedObjectTypeNode(
				new NodeId(0, NodeNavigator.getInstance().generateNewNodeId()));
		testObjectType.setBrowseName(new QualifiedName(0, "TestNode"));
		testObjectType.setDisplayName(new LocalizedText("en", "Test Display Name"));

		// get parent node to insert the test node at
		CachedBaseNode baseObjectTypeNode = NodeNavigator.getInstance()
				.navigateByName("Root/Types/ObjectTypes/BaseObjectType");

		// insert node
		NodeGenerator.getInstance().insertNode(testObjectType, baseObjectTypeNode);

		// check, if node had been inserted
		assertSame(testObjectType, NodeNavigator.getInstance()
				.navigateByName("Root/Types/ObjectTypes/BaseObjectType/" + testObjectType.getBrowseName().getName()));

		// check, if node has parent and parent has node as child
		assertNotNull(testObjectType.getParent());
		assertTrue(Lists.newArrayList(testObjectType.getParent().getChildren()).contains(testObjectType));

		// check, if parent has correct hierarchical reference
		CachedReference ref = new CachedReference(null, null, null, testObjectType.getNodeId());
		assertTrue(testObjectType.getParent().getReferences().contains(ref));
	}

	/** tests, if NodeGenerator.removeNode(node) removes the node correctly */
	@Test
	public void testRemoveNode() {
		NodeGenerator.getInstance().generateBaseTypes();

		// retrieve the node to remove
		CachedBaseNode toRemove = NodeNavigator.getInstance().navigateByName("Root/Types/ObjectTypes/BaseObjectType/");

		// Remove the node
		NodeGenerator.getInstance().removeNode(toRemove);

		// check, if the node is gone
		assertNull(NodeNavigator.getInstance().navigateByName("Root/Types/ObjectTypes/BaseObjectType/"));

		// check, if the associated reference at the parent node is gone as well
		CachedReference hierarchyRef = new CachedReference(null, null, null, toRemove.getNodeId());
		assertFalse(toRemove.getParent().getReferences().contains(hierarchyRef));

		// check, if the node had been removed from the parent's list of
		// children
		assertFalse(Lists.newArrayList(toRemove.getParent().getChildren()).contains(toRemove));
	}

	/**
	 * tests, if NodeGenerator.createAndInsert(...) works with an object node
	 */
	@Test
	public void testCreateAndInsertObject() {

		NodeGenerator.getInstance().generateBaseTypes();

		// retrieve the object-type for the object to create
		CachedObjectTypeNode serverTypeNode = (CachedObjectTypeNode) NodeNavigator.getInstance()
				.navigateByName("Root/Types/ObjectTypes/BaseObjectType/ServerType");

		// retrieve the parent where the object will be inserted
		CachedBaseNode objectsFolderNode = NodeNavigator.getInstance().navigateByName("Root/Objects");

		// try generating an object node called TestServer with the previously
		// retrieved object type
		NodeGenerator.getInstance().createAndInsert(AddDialogType.OBJECT, 0, "TestServer",
				NodeNavigator.getInstance().generateNewNodeId(), serverTypeNode, objectsFolderNode);

		// check, if the node had been created
		CachedBaseNode created = NodeNavigator.getInstance().navigateByName("Root/Objects/TestServer");
		assertNotNull(created);
		assertEquals(objectsFolderNode, created.getParent());

		// attention: might not work with a type that contains subtypes

		// check references
		// +1: instance will have an additional HasTypeDefinition reference
		assertEquals(serverTypeNode.getReferences().size() + 1, created.getReferences().size());
		assertEquals(serverTypeNode.getChildren().length, created.getChildren().length);

		// check references by BrowseName, because CachedReference.hashCode()
		// works on NodeId
		List<QualifiedName> origRefs = serverTypeNode.getReferences().stream().map(CachedReference::getBrowseName)
				.collect(Collectors.toList());
		List<QualifiedName> createdRefs = created.getReferences().stream().map(CachedReference::getBrowseName)
				.collect(Collectors.toList());

		origRefs.forEach(ref -> assertTrue(createdRefs.contains(ref)));

		// check children
		List<CachedBaseNode> childrenOrig = Lists.newArrayList(serverTypeNode.getChildren());
		List<CachedBaseNode> childrenCreated = Lists.newArrayList(created.getChildren());
		childrenOrig.forEach(child -> childrenCreated.contains(child));

		// check, if parent has HasComponent reference
		CachedReference hierarchyRef = new CachedReference(null, null, null, created.getNodeId());
		assertTrue(objectsFolderNode.getReferences().contains(hierarchyRef));

		// check, if created node has HasTypeDefinition reference
		CachedReference typeDefRef = new CachedReference(null, null, null, serverTypeNode.getNodeId());
		assertTrue(created.getReferences().contains(typeDefRef));
	}

}
