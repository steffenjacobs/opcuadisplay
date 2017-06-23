package me.steffenjacobs.opcuadisplay.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple multimap based on java.util.HashMap, that maps multiple values to
 * the same key
 * 
 * @author Steffen Jacobs
 */
public class MultiMap<K, V> {

	private final HashMap<K, List<V>> innerMap = new HashMap<>();

	/** add a value associated to a key */
	public void add(K k, V v) {
		List<V> values = innerMap.get(k);
		if (values == null) {
			values = new ArrayList<>();
		}
		values.add(v);
		innerMap.put(k, values);
	}

	/** remove a value associated to a key */
	public void remove(K k, V v) {
		List<V> values = innerMap.get(k);
		if (values == null) {
			return;
		}
		values.remove(v);

		if (values.isEmpty()) {
			innerMap.remove(k);
		} else {
			innerMap.put(k, values);
		}
	}

	/** @return the values associated with a key */
	public List<V> get(K k) {
		return innerMap.get(k);
	}

	/** remove all values associated to a key */
	public void removeall(K k) {
		innerMap.remove(k);
	}

	/**
	 * @return true: if the key is associated with any values<br>
	 * 		false: else
	 */
	public boolean contains(K k) {
		return innerMap.containsKey(k);
	}
}
