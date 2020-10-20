package vom.client.asm.web.trove;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class WebTrove implements Serializable {

  public static final ThreadLocal<WebTrove> WEB_TROVE = new ThreadLocal<WebTrove>();

  public static final ThreadLocal<Integer> execCount = new ThreadLocal<Integer>() {
    @Override
    protected Integer initialValue() {
      return 0;
    }
  };

  public static final List<String> logs =
    Collections.synchronizedList(new ArrayList<String>());

  public static final String WEB_TROVE_INTERNAL_NAME =
    Type.getInternalName(WebTrove.class);
  public static final String WEB_TROVE_SEIZE = "seize";
  public static final String WEB_TROVE_SEIZE_DESC =
    "(Ljavax/servlet/http/HttpServletRequest;J)V";
  public static final String WEB_TROVE_EXPEL = "expel";
  public static final String WEB_TROVE_EXPEL_DESC = "(J)V";
  public static final String WEB_TROVE_CHASE = "chase";
  public static final String WEB_TROVE_CHASE_DESC =
    "(Ljava/lang/String;Ljava/lang/String;J[Ljava/lang/Object;)V";

  private final String id;
  private final Long collected;
  private final String method;
  private final String uri;
  private Map<String, String> headers = new HashMap<String, String>();
  private Map<String, String[]> parameters = new HashMap<String, String[]>();


  public WebTrove(String id, Long collected, String method, String uri) {
    this.id = id;
    this.collected = collected;
    this.method = method;
    this.uri = uri;
  }


  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public void setParameters(Map<String, String[]> parameters) {
    this.parameters.putAll(parameters);
  }

  public static void seize(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      WEB_TROVE_INTERNAL_NAME,
      WEB_TROVE_SEIZE,
      WEB_TROVE_SEIZE_DESC,
      false);
  }

  @SuppressWarnings("unused")
  public static void seize(HttpServletRequest request, long started) {
    if (request == null) return;

    final WebTrove trove = new WebTrove(
      Config.getId(),
      started,
      request.getMethod(),
      request.getRequestURI()
    );

    WEB_TROVE.set(trove);
    execCount.set(execCount.get() + 1);


    @SuppressWarnings("unchecked") final Map<String, String[]> parameterMap
      = request.getParameterMap();
    trove.setParameters(parameterMap);

    @SuppressWarnings("unchecked") final Enumeration<String> headerNames =
      request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      final String name = headerNames.nextElement();
      trove.addHeader(name, request.getHeader(name));
    }

    logs.add(String.format(
      "%s-%d---seize [ %s : %s ] ----------%n",
      Thread.currentThread().getName(),
      execCount.get(),
      trove.getId(),
      new Date(trove.getCollected()).toString()
    ));

    logs.add(String.format(
      "%s-%d %s %s%n",
      Thread.currentThread().getName(),
      execCount.get(),
      trove.getMethod(),
      trove.getUri()
    ));

  }

  public static void expel(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      WEB_TROVE_INTERNAL_NAME,
      WEB_TROVE_EXPEL,
      WEB_TROVE_EXPEL_DESC,
      false);
  }

  @SuppressWarnings("unused")
  public static void expel(long elapsed) {
    WebTrove trove = WEB_TROVE.get();

    logs.add(String.format(
      "%s-%d elapsed: %d%n",
      Thread.currentThread().getName(),
      execCount.get(),
      elapsed
    ));
    logs.add(String.format(
      "%s-%d---expel [ %s : %s ] ---------- %n",
      Thread.currentThread().getName(),
      execCount.get(),
      trove.getId(),
      new Date(trove.getCollected()).toString()
    ));

    WEB_TROVE.remove();
  }

  public static void chase(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      WEB_TROVE_INTERNAL_NAME,
      WEB_TROVE_CHASE,
      WEB_TROVE_CHASE_DESC,
      false);
  }

  @SuppressWarnings("unused")
  public static void chase(
    String className,
    String methodName,
    long elapsed,
    Object[] parameter
  ) {
    final WebTrove trove = WEB_TROVE.get();
    if (trove == null) return;

    logs.add(String.format("%s-%d<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<%n", Thread.currentThread().getName(), execCount.get()));
    logs.add(String.format("%s-%d %s # %s (%d ms)%n", Thread.currentThread().getName(), execCount.get(), className, methodName, elapsed));
    logs.add(String.format("%s-%d %s%n", Thread.currentThread().getName(), execCount.get(), Arrays.toString(parameter)));
    logs.add(String.format("%s-%d>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>%n", Thread.currentThread().getName(), execCount.get()));
  }

  public String getId() {
    return id;
  }

  public Long getCollected() {
    return collected;
  }

  public String getMethod() {
    return method;
  }

  public String getUri() {
    return uri;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public Map<String, String[]> getParameters() {
    return parameters;
  }
}
