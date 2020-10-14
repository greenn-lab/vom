package vom.client.asm.web.trove;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.objectweb.asm.Type;
import vom.client.asm.jdbc.trove.DBTrove;
import vom.client.asm.web.servlet.HttpServletChaserAdapter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WebTrove implements Serializable {

  public static final ThreadLocal<WebTrove> WEB_TROVE = new ThreadLocal<WebTrove>();

  public static final String WEB_TROVE_INTERNAL_NAME =
    Type.getInternalName(WebTrove.class);
  public static final String WEB_TROVE_SEIZE = "seize";
  public static final String WEB_TROVE_SEIZE_DESC = "()V";

  private String id;
  private Long collected;
  private String uri;
  private Map<String, String> headers = new HashMap<String, String>();
  private Map<String, String> parameters = new HashMap<String, String>();


  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public void addParameter(String name, String value) {
    parameters.put(name, value);
  }

  public static void seize(String classAndMethod, Object[] parameters) {
    System.out.printf(
      "%s.swipe(%n\\tclassAndMethod: %s,%n\\tparameters: %s%n)%n",
      HttpServletChaserAdapter.class.getName(),
      classAndMethod,
      Arrays.toString(parameters)
    );

    WEB_TROVE.remove();
  }
}
