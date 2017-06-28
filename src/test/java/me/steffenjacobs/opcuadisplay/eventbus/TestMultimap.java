package me.steffenjacobs.opcuadisplay.eventbus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import me.steffenjacobs.opcuadisplay.eventbus.MultiMap;

public class TestMultimap {

	/**
	 * 1. add a list of objects to a multimap<br>
	 * 2. remove an object from the multimap <br>
	 * 3. check, if the object is no longer in the map <br>
	 * 4. remove all objects associated with a key from the multimap<br>
	 * 5. check, if all objects associated with the key had been removed from
	 * the multimap
	 */
	@Test
	public void integrationTest() {
		final MultiMap<String, String> map = new MultiMap<>();

		final String itemToRemove = "third";
		final List<String> strings = Lists.newArrayList("first", "second", "third", "fourth", "12345");

		final String key = "key";

		// add values
		strings.forEach(s -> map.add(key, s));

		// check, if values are still there
		assertEquals(strings, map.get(key));

		// test remove
		strings.remove(itemToRemove);
		map.remove(key, itemToRemove);

		assertEquals(strings, map.get(key));

		// test remove-all
		map.removeall(key);
		assertNull(map.get(key));
	}
}
