package vom.client.bci.trove;

import lombok.Getter;
import org.objectweb.asm.Type;

import java.io.Serializable;

@Getter
public class SQLChaser extends Chaser implements Serializable {

  public static final Type QUERY_TYPE = Type.getType(SQLChaser.class);
  public static final String QUERY_INTERNAL =
    Type.getInternalName(SQLChaser.class);

  private final String sql;


  public SQLChaser(String sql) {
    this.sql = sql;
  }

  @Override
  public void keep(Trove trove) {
    super.keep(trove);

    if (trove != null) {
      trove.setCurrentQuery(this);
    }
  }

  @Override
  public String signature() {
    return sql;
  }
}
