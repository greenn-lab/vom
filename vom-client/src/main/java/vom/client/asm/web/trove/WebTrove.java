package vom.client.asm.web.trove;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public class WebTrove implements Serializable {

  public static final ThreadLocal<WebTrove> WEB_TROVE = new ThreadLocal<WebTrove>();

  public static final String WEB_TROVE_INTERNAL_NAME =
    Type.getInternalName(WebTrove.class);
  public static final String WEB_TROVE_SEIZE = "seize";
  public static final String WEB_TROVE_SEIZE_DESC =
    "(Ljavax/servlet/http/HttpServletRequest;JJ)V";
  public static final String WEB_TROVE_CHASE = "chase";
  public static final String WEB_TROVE_CHASE_DESC =
    "(Ljava/lang/String;Ljava/lang/String;J[Ljava/lang/Object;)V";

  private final String id;
  private final Long collected;
  private final Long elapsed;
  private final String method;
  private final String uri;
  private Map<String, String> headers = new HashMap<String, String>();
  private Map<String, String[]> parameters = new HashMap<String, String[]>();


  public WebTrove(String id, Long collected, Long elapsed, String method, String uri) {
    this.id = id;
    this.collected = collected;
    this.elapsed = elapsed;
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
  public static void seize(HttpServletRequest request, long started, long finished) {
    if (request == null) return;

    final WebTrove trove = new WebTrove(
      Config.getId(),
      started,
      finished - started,
      request.getMethod(),
      request.getRequestURI()
    );

    @SuppressWarnings("unchecked") final Map<String, String[]> parameterMap
      = request.getParameterMap();
    trove.setParameters(parameterMap);

    @SuppressWarnings("unchecked") final Enumeration<String> headerNames =
      request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      final String name = headerNames.nextElement();
      trove.addHeader(name, request.getHeader(name));
    }

    WEB_TROVE.set(trove);
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
    System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    System.out.printf("%s # %s (%d ms)%n", className, methodName, elapsed);
    System.out.println(Arrays.toString(parameter));
    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
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
