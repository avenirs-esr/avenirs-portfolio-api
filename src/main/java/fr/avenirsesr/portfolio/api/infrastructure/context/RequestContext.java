package fr.avenirsesr.portfolio.api.infrastructure.context;

public class RequestContext {
  private static final ThreadLocal<RequestData> context = new ThreadLocal<>();

  public static void set(RequestData data) {
    context.set(data);
  }

  public static RequestData get() {
    return context.get();
  }

  public static void clear() {
    context.remove();
  }
}
