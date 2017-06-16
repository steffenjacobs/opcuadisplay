package me.steffenjacobs.opcuadisplay.shared.opcua;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
/** @author Steffen Jacobs */
public class FutureResolver {

	public static <T> T resolveFutureSafe(CompletableFuture<T> future) {
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException ee) {
			return null;
		}
	}
}
