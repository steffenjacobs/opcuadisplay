package me.steffenjacobs.opcuadisplay.shared.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventBus {

	private static EventBus INSTANCE;

	private HashMap<String, List<EventListener<Event>>> listeners = new HashMap<>();

	private EventBus() {
		// singleton
	}

	public static EventBus getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EventBus();
		}
		return INSTANCE;
	}

	public void fireEvent(Event event) {
		List<EventListener<Event>> list = listeners.get(event.identifier);
		if (list != null) {
			list.forEach(l -> l.onAction(event));
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void addListener(String identifier, EventListener<T> listener) {
		List<EventListener<Event>> list = listeners.get(identifier);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add((EventListener<Event>) listener);
		listeners.put(identifier, list);
	}

	public void removeListener(String identifier, EventListener<Event> listener) {
		List<EventListener<Event>> list = listeners.get(identifier);

		if (list == null) {
			return;
		}

		list.remove(listener);
		if (list.isEmpty()) {
			listeners.remove(identifier);
			return;
		}

		listeners.put(identifier, list);
	}

	public static abstract class EventArgs {
		public static EventArgs NONE = new EventArgs() {
		};
	};

	public static interface EventListener<T extends Event> {

		void onAction(T event);
	}

	public static abstract class Event {
		private final String identifier;
		private final EventArgs args;

		protected Event(String identifier, EventArgs args) {
			this.identifier = identifier;
			this.args = args;
		}

		public EventArgs getArgs() {
			return args;
		}
	}
}
