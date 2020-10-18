package vom.client.asm.trove;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Booty implements Serializable {

  protected final String signature;

  protected long elapse;

  private final List<Parameter> parameters = new ArrayList<Parameter>();


  public Booty(String signature) {
    this.signature = signature;
  }


  public void addParameter(Object value) {
    if (value != null) {
      parameters.add(new Parameter(value));
    }
  }


  public static class Parameter {

    private final Class<?> type;

    private final Object value;


    public Parameter(Object value) {
      this.type = value.getClass();
      this.value = value;
    }

  }

}
