package vom.client.bci.trove;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
public class QueryInChasing extends PointInChasing implements Serializable {

  private final String sql;

  @Setter
  private long elapsed;


  public QueryInChasing(String sql) {
    this.sql = sql;
  }

  @Override
  public String getSignature() {
    return sql;
  }
}
