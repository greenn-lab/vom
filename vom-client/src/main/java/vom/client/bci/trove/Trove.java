package vom.client.bci.trove;

import lombok.Getter;
import lombok.Setter;
import vom.client.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Trove implements Serializable {

  private final String id;
  private final Long collected;
  private final String method;
  private final String uri;

  private Map<String, Serializable> headers = new HashMap<String, Serializable>();
  private Map<String, String[]> parameters = new HashMap<String, String[]>();
  private List<Chaser> dregs = new ArrayList<Chaser>();
  private long finished;
  private SQLChaser currentQuery;
  private Throwable error;

  private final transient Object starter;

  public Trove(
    String method,
    String uri,
    Object starter
  ) {
    this.id = Config.getId();
    this.collected = System.currentTimeMillis();
    this.method = method;
    this.uri = uri;
    this.starter = starter;


  }


  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public void addBooty(Chaser booty) {
    dregs.add(booty);
  }

  public long elapse() {
    return finished - collected;
  }

  @Getter
  public static class Argument implements Serializable {

    private final Class<?> type;
    private final Serializable value;

    public Argument(Object value) {
      this.type = null == value ? null:value.getClass();
      this.value = value instanceof Serializable
        ? (Serializable) value
        :null;
    }

  }


}
