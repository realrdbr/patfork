package net.lopymine.patpat.client;

//? >=1.19.4 && fabric {

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class PatPatClientStatsLifecycleTest {

	@Test
	void autosaveDoesNotKeepJvmAliveAndStopsWithPendingTasks() throws Exception {
		Class<?> manager = Class.forName("net.lopymine.patpat.client.config.PatPatClientStatsConfig$AutoSaveManager");
		Method start = manager.getDeclaredMethod("start");
		Method stop = manager.getDeclaredMethod("stop");
		Field serviceField = manager.getDeclaredField("SERVICE");
		start.setAccessible(true);
		stop.setAccessible(true);
		serviceField.setAccessible(true);
		ScheduledExecutorService service = (ScheduledExecutorService) serviceField.get(null);

		try {
			start.invoke(null);
			assertTrue(service.submit(() -> Thread.currentThread().isDaemon()).get(5, TimeUnit.SECONDS),
					"Autosave must not prevent JVM exit if client initialization fails");
		} finally {
			stop.invoke(null);
		}

		assertTrue(service.isShutdown());
		assertTrue(service.awaitTermination(5, TimeUnit.SECONDS),
				"The five-minute autosave task must not delay client shutdown");
	}
}

//?}
