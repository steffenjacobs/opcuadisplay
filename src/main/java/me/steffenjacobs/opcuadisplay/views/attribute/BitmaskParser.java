package me.steffenjacobs.opcuadisplay.views.attribute;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import me.steffenjacobs.opcuadisplay.shared.widgets.GenericComboBox.Renderer;

/**
 * Bitmask Parser: converts a bitmasked UInteger to a map and backwards
 * 
 * @author Steffen Jacobs
 */
public class BitmaskParser {

	private static BitmaskParser instance;
	private final String[] names;

	private BitmaskParser() {
		// singleton

		// the order of these strings is important, because the index of each
		// string in this array determines the position of the associated bit in
		// the bitmasked UInteger
		names = new String[]
			{ "AccessLevel", "ArrayDimensions", "BrowseName", "ContainsNoLoops", "DataType", "Description",
					"DisplayName", "EventNotifier", "Executable", "Historizing", "InverseName", "IsAbstract",
					"MinimumSamplingInterval", "NodeClass", "NodeId", "Symmetric", "UserAccessLevel", "UserExecutable",
					"UserWriteMask", "ValueRank", "WriteMask", "ValueForVariableType" };
	}

	public static BitmaskParser getInstance() {
		if (instance == null) {
			instance = new BitmaskParser();
		}
		return instance;
	}

	/**
	 * converts a sorted map containing the flag name and the flag value into
	 * one bitmasked UInteger
	 */
	public UInteger toBitmask(Map<String, Boolean> data) {
		if (data == null) {
			return UInteger.MIN;
		}

		long result = 0;

		for (int i = 0; i < names.length; i++) {
			Boolean b = data.get(names[i]);
			result += (b != null && b) ? Math.pow(2, i) : 0;
		}
		return UInteger.valueOf(result);
	}

	/**
	 * converts a bitmasked UInteger into a map containing the flag name and the
	 * value for each flag
	 */
	public Map<String, Boolean> fromBitmask(UInteger bitmask) {
		Map<String, Boolean> result = new LinkedHashMap<>();
		long b = bitmask.longValue();

		for (int i = 0; i < names.length; ++i) {
			// extract the i-th bit from the bitmasked value
			boolean bitValue = ((b & 1 << i) >> i) > 0;

			result.put(names[i], bitValue);
		}

		return result;
	}

	/**
	 * @returns a renderer that converts the map containing flag name and value
	 *          for each flag into a String containing the bitmasked UInteger
	 */
	public Renderer<Map<String, Boolean>> createRenderer() {
		return new Renderer<Map<String, Boolean>>() {
			@Override
			public String render(Map<String, Boolean> obj) {
				return BitmaskParser.getInstance().toBitmask(obj).toString();
			}
		};
	}
}
