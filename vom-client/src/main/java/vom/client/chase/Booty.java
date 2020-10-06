package vom.client.chase;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Booty implements Serializable {
  
  private String id;
  private Long collected;
  private String uri;
  private Map<String, String> headers = new HashMap<>();
  private Map<? extends Serializable, ? extends Serializable> parameters;
  
  private Deque<Trove> troves = new ArrayDeque<>(5);
  
  public void addHeader(String name, String value) {
    headers.put(name, value);
  }
  
  public void addTrove(String qualifiedClass, String method, long elapsed, Throwable... error) {
    troves.offer(
        Trove.builder()
            .signature(qualifiedClass + '#' + method)
            .elapsed(elapsed)
            .error(error.length > 0 ? error[0] : null)
            .build()
    );
  }
  
  
  @Builder
  private static class Trove implements Serializable {
    
    private String signature;
    private List<? extends Serializable> arguments;
    private long elapsed;
    private Throwable error;
    
  }
  
}
