package me.steffenjacobs.opcuadisplay.opcInterface.opcClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/** @author Steffen Jacobs */
public class FutureResolver {

	/** @return the result of the get()-method of <i>future</i> */
	public static <T> T resolveFutureSafe(CompletableFuture<T> future) {
		try {
			return future.get();
		} catch (ExecutionException | InterruptedException ee) {
			return null;
		}
	}
}
