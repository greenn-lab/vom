package vom.client.bci.trove;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import vom.client.Config;
import vom.client.fastjson.ArgumentValueCodec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Trove implements Serializable {

  private final String id;
  private final Long collected;
  private String method;
  private String uri;

  private Map<String, Serializable> headers = new HashMap<String, Serializable>();
  private Map<String, String> parameters = new HashMap<String, String>();

  private long finished;
  private Throwable error;

  private List<Chaser> dregs = new ArrayList<Chaser>();

  @JSONField(serialize = false)
  private transient Object starter;

  @JSONField(serialize = false)
  private transient Object identifier;

  @JSONField(serialize = false)
  private transient SQLChaser currentQuery;


  public Trove() {
    this.id = Config.getId();
    this.collected = System.currentTimeMillis();
  }

  public Trove(String method, String uri, Object starter, Object identifier) {
    this();
    this.method = method;
    this.uri = uri;
    this.starter = starter;
    this.identifier = identifier;
  }


  public void addParameter(String name, String[] values) {
    String value = "";
    if (null != values) {
      value = values.length == 1 ? values[0]:Arrays.toString(values);
    }

    parameters.put(name, value);
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

    @JSONField(serializeUsing = ArgumentValueCodec.class)
    private final Serializable value;

    public Argument(Object value) {
      this.type = null == value ? null:value.getClass();
      this.value = value instanceof Serializable
        ? (Serializable) value
        :null;
    }

  }

}
