package vom.client.asm.web.trove;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vom.client.asm.jdbc.trove.DBTrove;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class WebTrove implements Serializable {

  public static final ThreadLocal<WebTrove> WEB_TROVE = new ThreadLocal<WebTrove>();

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

}
