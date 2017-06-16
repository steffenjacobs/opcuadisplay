package me.steffenjacobs.opcuadisplay.management.event.eventbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Display;

import me.steffenjacobs.opcuadisplay.ui.views.CloseableView;

/** @author Steffen Jacobs */
public class EventBus {

	private class RegisteredEvent {
		String eventIdentifier;
		EventListener<Event> listener;

		public RegisteredEvent(String eventIdentifier, EventListener<Event> listener) {
			super();
			this.eventIdentifier = eventIdentifier;
			this.listener = listener;
		}
	}

	private static EventBus INSTANCE;

	private HashMap<String, List<EventListener<Event>>> listeners = new HashMap<>();

	private MultiMap<String, RegisteredEvent> registeredEvents = new MultiMap<>();

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
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				List<EventListener<Event>> list = listeners.get(event.identifier);
				if (list != null) {
					list.forEach(l -> l.onAction(event));
				}
			}
		});
	}

	public <T extends Event> void addListener(CloseableView registerer, String eventIdentifier,
			EventListener<T> listener) {
		this.addListener(registerer.getIdentifier(), eventIdentifier, listener);
	}

	@SuppressWarnings("unchecked")
	public <T extends Event> void addListener(String registererIdentifier, String eventIdentifier,
			EventListener<T> listener) {
		List<EventListener<Event>> list = listeners.get(eventIdentifier);
		if (list == null) {
			list = new ArrayList<>();
		}
		list.add((EventListener<Event>) listener);
		listeners.put(eventIdentifier, list);

		registeredEvents.add(registererIdentifier,
				new RegisteredEvent(eventIdentifier, (EventListener<Event>) listener));
	}

	public void unregisterAllListeners(final CloseableView registerer) {
		if (!registeredEvents.contains(registerer.getIdentifier())) {
			return;
		}
		registeredEvents.get(registerer.getIdentifier()).stream()
				.forEach(l -> unregisterListener(registerer, l.eventIdentifier, l.listener));
	}

	public void unregisterListener(CloseableView registerer, String eventIdentifier, EventListener<Event> listener) {
		List<EventListener<Event>> list = listeners.get(eventIdentifier);

		if (list == null) {
			return;
		}

		list.remove(listener);
		if (list.isEmpty()) {
			listeners.remove(eventIdentifier);
			return;
		}

		listeners.put(eventIdentifier, list);
		registeredEvents.remove(registerer.getIdentifier(), new RegisteredEvent(eventIdentifier, listener));
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
