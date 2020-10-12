package vom.client.asm.jdbc.trove;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.objectweb.asm.Type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Builder
public class DBTrove extends ArrayList<DBTrove.Booty> {

  public static final ThreadLocal<DBTrove> DB_TROVE = new ThreadLocal<DBTrove>();

  public static final String DB_TROVE_INTERNAL_NAME =
    Type.getInternalName(DBTrove.class);

  public static final String DB_TROVE_SEIZE = "seize";
  public static final String DB_TROVE_SEIZE_DESC = "()V";
  public static final String DB_TROVE_ADD_SQL = "addSql";
  public static final String DB_TROVE_ADD_SQL_DESC = "(Ljava/lang/String;)V";
  public static final String DB_TROVE_ADD_PARAM = "addParameter";
  public static final String DB_TROVE_ADD_PARAM_DESC = "(Ljava/lang/Object;)V";

  @SuppressWarnings("unused")
  public static void addSql(String sql) {
    DBTrove trove = DB_TROVE.get();
    if (trove == null) {
      trove = new DBTrove();
      DB_TROVE.set(trove);
    }

    System.out.printf("trove sql: %s%n", sql);

    trove.add(new DBTrove.Booty(sql));
  }

  @SuppressWarnings("unused")
  public static void addParameter(Object value) {
    final DBTrove trove = DB_TROVE.get();
    if (trove != null) {
      trove.get(trove.size() - 1).addParameter(value);
    }
  }

  @SuppressWarnings("unused")
  public static void seize() {
    final DBTrove trove = DB_TROVE.get();
    if (trove != null) {
      for (DBTrove.Booty booty : trove) {
        System.out.printf("seized ----%n %s %n---%n", booty.toString());
      }
    }
  }


  @Builder
  @ToString
  protected static class Booty implements Serializable {

    @Getter
    private final String sql;

    private final List<Parameter> parameters = new ArrayList<Parameter>();


    public Booty(String sql) {
      this.sql = sql;
    }


    public void addParameter(Object value) {
      final Parameter build = Parameter.builder()
        .type(value.getClass())
        .value(value)
        .build();

      parameters.add(build);
    }

  }

  @RequiredArgsConstructor
  @Builder
  @ToString
  private static class Parameter implements Serializable {
    private final Class<?> type;
    private final transient Object value;
  }

}
