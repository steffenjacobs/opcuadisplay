package me.steffenjacobs.opcuadisplay.shared.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.junit.Test;

import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.Event;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventArgs;
import me.steffenjacobs.opcuadisplay.shared.eventbus.EventBus.EventListener;
import me.steffenjacobs.opcuadisplay.views.CloseableView;

/** @author Steffen Jacobs */
public class TestEventBus {

	/** TestEvent class with only an identifier */
	private class TestEvent extends Event {

		public static final String IDENTIFIER = "testEvent";

		public TestEvent() {
			super(IDENTIFIER, EventArgs.NONE);
		}
	}

	/** Dummy test view only with an identifier */
	private class TestView extends CloseableView {

		@Override
		public String getIdentifier() {
			return "testIdentifier";
		}

		@Override
		public void createPartControl(Composite arg0) {
			// dummy
		}

		@Override
		public void setFocus() {
			// dummy
		}

	}

	/** tests, whether the event bus is a singleton */
	@Test
	public void testSingleton() {
		assertSame(EventBus.getInstance(), EventBus.getInstance());
	}

	/**
	 * 1. create a event listener<br>
	 * 2. add the event listener to the event bus<br>
	 * 3. fire an event<br>
	 * 4. check, if the event had been received<br>
	 * 5. remove the event listener<br>
	 * 6. fire an event<br>
	 * 7. check, if the event had not been received
	 */
	@Test
	public void integrationTest() throws InterruptedException {

		final Semaphore waitForEvent = new Semaphore(1);

		final TestView testView = new TestView();
		final TestEvent testEvent = new TestEvent();
		final EventListener<TestEvent> listener = new EventListener<TestEvent>() {

			@Override
			public void onAction(TestEvent event) {
				assertSame(testEvent, event);
				waitForEvent.release();
			}
		};

		// add listener
		EventBus.getInstance().addListener(testView, TestEvent.IDENTIFIER, listener);

		// test, if event is forwarded to listener
		EventBus.getInstance().fireEvent(testEvent);
		assertTrue(waitForEvent.tryAcquire(2, 500, TimeUnit.MILLISECONDS));

		waitForEvent.drainPermits();

		// unregister listener
		EventBus.getInstance().unregisterAllListeners(testView);

		// check, if event is no longer forwarded to listener
		EventBus.getInstance().fireEvent(testEvent);

		Thread.sleep(500);
		assertEquals(0, waitForEvent.availablePermits());
	}
}
