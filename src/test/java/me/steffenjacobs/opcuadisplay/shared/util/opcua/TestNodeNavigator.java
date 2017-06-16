package me.steffenjacobs.opcuadisplay.shared.util.opcua;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.junit.Test;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectNode;
import me.steffenjacobs.opcuadisplay.shared.domain.CachedObjectTypeNode;
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeGenerator;
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeNavigator;
import me.steffenjacobs.opcuadisplay.shared.opcua.NodeNavigator.NodeManipulator;

public class TestNodeNavigator {

	@Test
	public void testSingleton() {
		assertSame(NodeNavigator.getInstance(), NodeNavigator.getInstance());
	}

	@Test
	public void testGenerateNewNodeId() {

		final List<Integer> list = new CopyOnWriteArrayList<>();
		for (int i = 0; i < 50; i++) {
			new Thread(() -> {
				for (int j = 0; j < 5000; j++) {
					int id = NodeNavigator.getInstance().generateNewNodeId();
					assertFalse(list.contains(id));
					list.add(id);
				}
			});
		}
	}

	@Test
	public void testNodeId() {
		int id = NodeNavigator.getInstance().getHighestNodeId();

		int newId = NodeNavigator.getInstance().generateNewNodeId();

		assertEquals(id + 1, newId);
		assertEquals(newId, NodeNavigator.getInstance().getHighestNodeId());

		NodeNavigator.getInstance().resetHighestNodeId();

		assertEquals(-1, NodeNavigator.getInstance().getHighestNodeId());
	}

	@Test
	public void testIncreaseHighestNodeIdIfNecessarySafe() {

		NodeNavigator.getInstance().resetHighestNodeId();
		CachedBaseNode testNode = new CachedObjectTypeNode(new NodeId(0, 10));
		for (int i = 0; i < 5; i++) {
			NodeNavigator.getInstance().generateNewNodeId();
		}

		NodeNavigator.getInstance().increaseHighestNodeIdIfNecessarySafe(testNode);

		assertEquals(((UInteger) testNode.getNodeId().getIdentifier()).intValue(),
				NodeNavigator.getInstance().getHighestNodeId());
	}

	@Test
	public void testSetGetRoot() {
		CachedObjectNode root = new CachedObjectNode(new NodeId(0, NodeNavigator.getInstance().generateNewNodeId()));

		NodeNavigator.getInstance().setRoot(root);

		assertSame(root, NodeNavigator.getInstance().getRoot());
	}

	private CachedBaseNode[] generateTestStructure() {
		CachedObjectNode root = new CachedObjectNode(new NodeId(0, NodeNavigator.getInstance().generateNewNodeId()));
		root.setBrowseName(new QualifiedName(0, "Root"));
		CachedObjectNode test1 = new CachedObjectNode(new NodeId(0, NodeNavigator.getInstance().generateNewNodeId()));
		test1.setBrowseName(new QualifiedName(0, "Test1"));
		CachedObjectNode test2 = new CachedObjectNode(new NodeId(0, NodeNavigator.getInstance().generateNewNodeId()));
		test2.setBrowseName(new QualifiedName(0, "Test2"));

		NodeNavigator.getInstance().setRoot(root);

		NodeGenerator.getInstance().insertNode(test1, root, false);
		NodeGenerator.getInstance().insertNode(test2, test1, false);

		return new CachedBaseNode[]
			{ root, test1, test2 };
	}

	/** tests NodeNavigator.pathAsString() and NodeNavigator.getPath() */
	@Test
	public void testPathAsString() {
		CachedBaseNode[] nodes = generateTestStructure();

		assertEquals("/Root", NodeNavigator.getInstance().pathAsString(nodes[0]));
		assertEquals("/Root/Test1", NodeNavigator.getInstance().pathAsString(nodes[1]));
		assertEquals("/Root/Test1/Test2", NodeNavigator.getInstance().pathAsString(nodes[2]));
	}

	@Test
	public void testNavigateByName() {
		CachedBaseNode[] nodes = generateTestStructure();

		assertEquals(nodes[2], NodeNavigator.getInstance().navigateByName("Root/Test1/Test2"));
		assertEquals(nodes[2], NodeNavigator.getInstance().navigateByName("Test1/Test2", nodes[0]));
	}

	@Test
	public void testIterateNodes() {
		final List<CachedBaseNode> list = new CopyOnWriteArrayList<>();

		CachedBaseNode[] nodes = generateTestStructure();
		NodeNavigator.getInstance().iterateNodes(nodes[0], new NodeManipulator() {

			@Override
			public void manipulate(CachedBaseNode cbn) {
				list.add(cbn);
			}
		});

		List<CachedBaseNode> orig = Lists.newArrayList(nodes);

		assertEquals(orig.size(), list.size());
		orig.forEach(n -> assertTrue(list.contains(n)));
	}

	@Test
	public void testGetTypeDefinition() {
		NodeGenerator.getInstance().generateBaseTypes();

		CachedBaseNode generatedServerNode = NodeNavigator.getInstance().navigateByName("Root/Objects/Server");

		assertNotNull(NodeNavigator.getInstance().getTypeDefinition(generatedServerNode).orElse(null));
	}

	@Test
	public void testAggregateSubTypes() {
		NodeGenerator.getInstance().generateBaseTypes();

		CachedBaseNode ref = NodeNavigator.getInstance()
				.navigateByName("Root/Types/ReferenceTypes/References/HierarchicalReferences/HasChild/Aggregates");

		List<CachedBaseNode> nodes = NodeNavigator.getInstance().aggregateSubTypes(ref);

		assertEquals(5, nodes.size());

		// check, if subtypes are as expected
		CachedBaseNode n1 = NodeNavigator.getInstance()
				.navigateByName("Root/Types/ReferenceTypes/References/HierarchicalReferences/HasChild/Aggregates/");
		CachedBaseNode n2 = NodeNavigator.getInstance().navigateByName(
				"Root/Types/ReferenceTypes/References/HierarchicalReferences/HasChild/Aggregates/HasProperty");
		CachedBaseNode n3 = NodeNavigator.getInstance().navigateByName(
				"Root/Types/ReferenceTypes/References/HierarchicalReferences/HasChild/Aggregates/HasComponent");
		CachedBaseNode n4 = NodeNavigator.getInstance().navigateByName(
				"Root/Types/ReferenceTypes/References/HierarchicalReferences/HasChild/Aggregates/HasComponent/HasOrderedComponent");
		CachedBaseNode n5 = NodeNavigator.getInstance().navigateByName(
				"Root/Types/ReferenceTypes/References/HierarchicalReferences/HasChild/Aggregates/HasHistoricalConfiguration");

		List<CachedBaseNode> expected = Lists.newArrayList(n1, n2, n3, n4, n5);

		expected.forEach(n -> assertTrue(nodes.contains(n)));
	}

	@Test
	public void testAggregateInheritedChildren() {
		NodeGenerator.getInstance().generateBaseTypes();

		CachedBaseNode ref = NodeNavigator.getInstance()
				.navigateByName("Root/Types/ObjectTypes/BaseObjectType/ServerRedundancyType/TransparentRedundancyType");

		List<CachedBaseNode> nodes = NodeNavigator.getInstance().aggregateInheritedChildren(ref);

		assertEquals(3, nodes.size());

		// check, if subtypes are as expected
		CachedBaseNode n1 = NodeNavigator.getInstance()
				.navigateByName("Root/Types/ObjectTypes/BaseObjectType/ServerRedundancyType/RedundancySupport");
		CachedBaseNode n2 = NodeNavigator.getInstance().navigateByName(
				"Root/Types/ObjectTypes/BaseObjectType/ServerRedundancyType/TransparentRedundancyType/CurrentServerId");
		CachedBaseNode n3 = NodeNavigator.getInstance().navigateByName(
				"Root/Types/ObjectTypes/BaseObjectType/ServerRedundancyType/TransparentRedundancyType/RedundantServerArray");

		List<CachedBaseNode> expected = Lists.newArrayList(n1, n2, n3);

		expected.forEach(n -> assertTrue(nodes.contains(n)));
	}

}
