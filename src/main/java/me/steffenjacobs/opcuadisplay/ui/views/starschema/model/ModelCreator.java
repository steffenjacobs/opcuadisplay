package me.steffenjacobs.opcuadisplay.ui.views.starschema.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.management.node.domain.CachedBaseNode;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.ColorSet;
import me.steffenjacobs.opcuadisplay.ui.views.starschema.SpiralLogic;

public class ModelCreator {

	private static ModelCreator instance;

	private static final int[][] SEQ_3x3 =
		{
				{ 1, 1 },
				{ 2, 1 },
				{ 0, 1 },
				{ 1, 2 },
				{ 1, 0 },
				{ 0, 0 },
				{ 0, 2 },
				{ 2, 2 },
				{ 2, 0 } };

	private static final int[][] SEQ_5x5 =
		{
				{ 2, 2 },

				{ 3, 2 },
				{ 1, 2 },
				{ 2, 3 },
				{ 2, 1 },

				{ 1, 1 },
				{ 3, 1 },
				{ 3, 3 },
				{ 1, 3 },

				{ 4, 2 },
				{ 0, 2 },
				{ 2, 4 },
				{ 2, 0 },

				{ 4, 3 },
				{ 1, 4 },
				{ 0, 1 },
				{ 3, 0 },

				{ 4, 1 },
				{ 3, 4 },
				{ 0, 3 },
				{ 1, 0 },

				{ 0, 0 },
				{ 4, 0 },
				{ 0, 4 },
				{ 4, 4 } };

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

		int ceil = (int) Math.ceil(Math.sqrt(list.size()));
		ceil = ceil % 2 == 0 ? ceil + 1 : ceil;

		list = fillUpEmptySpaces(list, ceil);

		if (ceil <= 5) {
			list = createStarSchemaManually(list);
		} else {
			list = createSpiral(list);
		}

		list = cropList(list, ceil);

		return new Model(list);
	}

	/**
	 * removes the top line from the star schema if the line contains only empty
	 * models
	 */
	private List<NodeModel> cropList(List<NodeModel> list, int ceil) {
		boolean empty = true;
		for (int i = 0; i < ceil; i++) {
			if (list.get(i) != NodeModel.EMPTY_NODE) {
				empty = false;
				break;
			}
		}
		if (empty) {
			for (int i = 0; i < ceil; i++) {
				list.remove(0);
			}
		}
		return list;
	}

	/**
	 * fill list up with empty nodes until the list size is the next bigger
	 * square number
	 */
	private List<NodeModel> fillUpEmptySpaces(List<NodeModel> list, int ceil) {
		// fill up empty spaces
		while (list.size() < ceil * ceil) {
			list.add(NodeModel.EMPTY_NODE);
		}
		return list;
	}

	/**
	 * create the star schema in a spiral from inside out, if more then 25 nodes
	 * to display
	 */
	private List<NodeModel> createSpiral(List<NodeModel> list) {
		int ceil = (int) Math.ceil(Math.sqrt(list.size()));
		ceil = ceil % 2 == 0 ? ceil + 1 : ceil;

		int[][] rd = new SpiralLogic(ceil, ceil).getData();

		NodeModel[][] arr = new NodeModel[ceil][];
		// init array
		for (int i = 0; i < arr.length; i++) {
			arr[i] = new NodeModel[ceil];
		}

		// copy data from list to correct array field
		for (int i = 0; i < rd.length; i++) {
			arr[rd[i][0]][rd[i][1]] = list.get(i);
		}

		List<NodeModel> result = new ArrayList<>();

		// flatten out array
		result = flatList(arr);

		return result;
	}

	/** create the star schema, if <= 25 nodes to display */
	private List<NodeModel> createStarSchemaManually(List<NodeModel> list) {
		List<NodeModel> result = new ArrayList<>();
		if (list.size() == 1) {
			result.add(list.get(0));
		} else if (list.size() <= 9) {

			int counter = 0;
			NodeModel[][] nm = new NodeModel[3][3];
			for (int[] step : SEQ_3x3) {
				if (insert(nm, step[1], step[0], list.get(counter++))) {
					break;
				}
			}
			result = flatList(nm);
		} else if (list.size() <= 25) {

			int counter = 0;
			NodeModel[][] nm = new NodeModel[5][5];
			for (int[] step : SEQ_5x5) {
				if (insert(nm, step[1], step[0], list.get(counter++))) {
					break;
				}
			}
			result = flatList(nm);
		}

		return result;
	}

	/** make a 1-dimensional list from the 2-dimensional array */
	private List<NodeModel> flatList(NodeModel[][] arr) {
		List<NodeModel> result = new ArrayList<>();
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				result.add(arr[i][j]);
			}
		}
		return result;
	}

	/**
	 * insert a node in an array at a position and return true, if
	 * ArrayIndexOutOfBoundsException occured
	 */
	private boolean insert(NodeModel[][] nm, int x, int y, NodeModel val) {
		try {
			nm[x][y] = val;
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}

	/**
	 * create a list of all child nodes and the parent node from a parent node
	 * cbn
	 */
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
}
