package vom.client.asm.trove;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Booty implements Serializable {

  protected final String signature;

  @Setter
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


  @Getter
  public static class Parameter {

    private final Class<?> type;

    private final Object value;


    public Parameter(Object value) {
      this.type = value.getClass();
      this.value = value;
    }

  }

}
