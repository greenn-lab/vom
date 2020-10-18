package vom.client.asm.web.trove;

import org.objectweb.asm.Type;
import vom.client.Config;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class WebTrove implements Serializable {

  public static final ThreadLocal<WebTrove> WEB_TROVE = new ThreadLocal<WebTrove>();

  public static final String WEB_TROVE_INTERNAL_NAME =
    Type.getInternalName(WebTrove.class);
  public static final String WEB_TROVE_SEIZE = "seize";
  public static final String WEB_TROVE_SEIZE_DESC =
    "(Ljavax/servlet/http/HttpServletRequest;)V";
  public static final String WEB_TROVE_EXPEL = "expel";
  public static final String WEB_TROVE_EXPEL_DESC = "()V";

  private String id;
  private Long collected;
  private String uri;
  private Map<String, String> headers = new HashMap<String, String>();
  private Map<String, String> parameters = new HashMap<String, String>();


  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  @SuppressWarnings("unused")
  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }

  @SuppressWarnings("unused")
  public static void seize(HttpServletRequest request) {
    if (request == null) return;
//    final Map<String, String[]> parameters =
//      unmodifiableMap(request.getParameterMap());
//
//    final WebTrove trove = WebTrove.builder()
//      .id(Config.getId())
//      .collected(System.currentTimeMillis())
//      .uri(request.getRequestURI())
//      .parameters(parameters)
//      .build();
//
//    final Enumeration<String> headerNames =
//      request.getHeaderNames();
//
//    while (headerNames.hasMoreElements()) {
//      final String name = headerNames.nextElement();
//      trove.addHeader(name, request.getHeader(name));
//    }
//
//    System.err.printf("swipe(request): %d%n", trove.getCollected());
//    WEB_TROVE.set(trove);
  }

  @SuppressWarnings("unused")
  public static void expel() {
    WEB_TROVE.remove();
  }

}
