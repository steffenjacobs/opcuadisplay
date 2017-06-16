package me.steffenjacobs.opcuadisplay.management.event.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/** @author Steffen Jacobs */
public class MultiMap<K, V> {

	private final HashMap<K, List<V>> innerMap = new HashMap<>();

	public void add(K k, V v) {
		List<V> values = innerMap.get(k);
		if (values == null) {
			values = new ArrayList<>();
		}
		values.add(v);
		innerMap.put(k, values);
	}

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
	
	public List<V> get(K k){
		return innerMap.get(k);
	}
	
	public void removeall(K k){
		innerMap.remove(k);
	}

	public boolean contains(K k) {
		return innerMap.containsKey(k);
	}
}
