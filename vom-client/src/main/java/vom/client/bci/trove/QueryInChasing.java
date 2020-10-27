package vom.client.bci.trove;

import lombok.Getter;
import org.objectweb.asm.Type;

import java.io.Serializable;

@Getter
public class QueryInChasing extends Chasing implements Serializable {

  public static final Type QUERY_TYPE = Type.getType(QueryInChasing.class);
  public static final String QUERY_INTERNAL =
    Type.getInternalName(QueryInChasing.class);
  public static final String QUERY_CONSTRUCTOR_DESC = "(Ljava/lang/String;)V";

  private final String sql;


  public QueryInChasing(String sql) {
    this.sql = sql;
  }

}
