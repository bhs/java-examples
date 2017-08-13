package io.opentracing.contrib.examples.activate_deactivate;

import io.opentracing.Scope;
import io.opentracing.Scope.Observer;
import io.opentracing.ScopeManager;
import io.opentracing.Span;
import io.opentracing.tag.Tags;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callback which executed at some unpredictable time. We don't know when it is started, when it is
 * completed. We cannot check status of it (started or completed)
 */
public class Callback implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(Callback.class);

  private final Span continuation;

  Callback(Span activeSpan) {
    continuation = activeSpan;
    logger.info("Callback created");
  }

  @Override
  public void run() {
    logger.info("Callback started");

    try (Scope scope = continuation.activate(Observer.FINISH_ON_CLOSE)) {
      TimeUnit.SECONDS.sleep(1);
      logger.info("Callback finished");
      scope.span().setTag(Tags.HTTP_STATUS.getKey(), 200); // we need it to test that finished span has it
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
