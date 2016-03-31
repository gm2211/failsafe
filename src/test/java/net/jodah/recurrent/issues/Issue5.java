package net.jodah.recurrent.issues;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import net.jodah.concurrentunit.Waiter;
import net.jodah.recurrent.Recurrent;
import net.jodah.recurrent.RecurrentFuture;
import net.jodah.recurrent.RetryPolicy;

@Test
public class Issue5 {
  /**
   * Asserts that a failure is handled as expected by a listener registered via whenFailure.
   */
  public void test() throws Throwable {
    Waiter waiter = new Waiter();

    RetryPolicy retryPolicy = new RetryPolicy().withDelay(100, TimeUnit.MILLISECONDS)
        .withMaxDuration(2, TimeUnit.SECONDS)
        .withMaxRetries(3)
        .retryFor(null);

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    RecurrentFuture<?> run = Recurrent.get(() -> {
      return null;
    } , retryPolicy, executor);

    run.whenFailure((result, failure) -> {
      waiter.assertNull(result);
      waiter.assertNull(failure);
      waiter.resume();
    });

    waiter.await(1000);
  }
}
