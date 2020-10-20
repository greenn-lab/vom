package vom.client.bci.trove;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class QueryInChasing implements PointInChasing, Serializable {

  private final String sql;

  private List<TroveArgument> arguments = null;

  @Setter
  private long elapsed;


  public QueryInChasing(String sql) {
    this.sql = sql;
  }


  public void addArgument(Object argument) {
    if (arguments == null) {
      arguments = new ArrayList<TroveArgument>();
    }

    arguments.add(new TroveArgument(argument));
  }

}
