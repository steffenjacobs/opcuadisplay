package me.steffenjacobs.opcuadisplay.views.starschema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.shared.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.views.starschema.ColorSet;

public class ModelCreator {

	private static ModelCreator instance;

	public static ModelCreator getInstance() {
		if (instance == null) {
			instance = new ModelCreator();
		}
		return instance;
	}

	private ModelCreator() {
		// singleton
	}

	public Model createModel(CachedBaseNode cbn) {
		List<NodeModel> list = createList(cbn);
		list = fillUp(list);
		list = moveFirstToCenter(list);
		return new Model(list);
	}

	private List<NodeModel> createList(CachedBaseNode cbn) {
		if (cbn == null) {
			return new ArrayList<>();
		}

		NodeModel parent = new NodeModel(cbn, ColorSet.SELECTED_NODE.getColor(), false);

		List<NodeModel> models = Lists.newArrayList(cbn.getChildren()).stream().map(n -> new NodeModel(n))
				.collect(Collectors.toList());

		models.forEach(m -> {
			NodeConnectionModel connection = new NodeConnectionModel();
			connection.setSource(parent);
			connection.setTarget(m);
			parent.addSourceConnection(connection);
			m.addTargetConnection(connection);
		});

		// move parent to front
		models.add(0, parent);
		return models;
	}

	/**
	 * fill list up with empty nodes until the list size is the next bigger
	 * square number
	 */
	private List<NodeModel> fillUp(List<NodeModel> list) {
		int ceil = (int) Math.ceil(Math.sqrt(list.size()));
		while (list.size() < ceil * ceil) {
			list.add(NodeModel.EMPTY_NODE);
		}

		return list;
	}

	/** move first node in the center */
	private List<NodeModel> moveFirstToCenter(List<NodeModel> list) {
		list.add(list.size() / 2, list.remove(0));
		return list;
	}
}
