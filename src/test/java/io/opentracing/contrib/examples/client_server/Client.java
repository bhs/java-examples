package io.opentracing.contrib.examples.client_server;

import io.opentracing.Scope;
import io.opentracing.Scope.Observer;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format.Builtin;
import io.opentracing.propagation.TextMapInjectAdapter;
import io.opentracing.tag.Tags;
import java.util.concurrent.ArrayBlockingQueue;

public class Client {

  private final ArrayBlockingQueue<Message> queue;
  private final Tracer tracer;

  public Client(ArrayBlockingQueue<Message> queue, Tracer tracer) {
    this.queue = queue;
    this.tracer = tracer;
  }

  public void send() {
    Message message = new Message();

    Scope scope = tracer.buildSpan("send").startActive(Observer.FINISH_ON_CLOSE);
    scope.span().setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);
    tracer.inject(scope.span().context(), Builtin.TEXT_MAP, new TextMapInjectAdapter(message));

    try {
      queue.put(message);
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      scope.close();
    }
  }

}
