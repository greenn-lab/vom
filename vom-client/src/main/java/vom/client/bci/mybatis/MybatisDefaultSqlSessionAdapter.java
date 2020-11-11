package vom.client.bci.mybatis;

import org.objectweb.asm.MethodVisitor;
import vom.client.bci.ClassVisitAdapter;

import java.util.HashMap;
import java.util.Map;

public class MybatisDefaultSqlSessionAdapter extends ClassVisitAdapter {

  private static final String INTERNAL_NAME;
  private static final Map<String, String> METHODS;

  static {
    INTERNAL_NAME = "org/apache/ibatis/session/defaults/DefaultSqlSession";

    METHODS = new HashMap<String, String>(3);

    METHODS.put("selectCursor",
      "(Ljava/lang/String;Ljava/lang/Object;Lorg/apache/ibatis/session/RowBounds;)");
    METHODS.put("selectList",
      "(Ljava/lang/String;Ljava/lang/Object;Lorg/apache/ibatis/session/RowBounds;)");
    METHODS.put("select",
      "(Ljava/lang/String;Ljava/lang/Object;Lorg/apache/ibatis/session/RowBounds;Lorg/apache/ibatis/session/ResultHandler;)");
    METHODS.put("update",
      "(Ljava/lang/String;Ljava/lang/Object;)");
  }


  public MybatisDefaultSqlSessionAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return INTERNAL_NAME.equals(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return METHODS.containsKey(methodName)
      && descriptor.startsWith(METHODS.get(methodName));
  }

  @Override
  public MethodVisitor methodVisitor(MethodVisitor visitor, String methodName, String descriptor) {
    return new MybatisDefaultSqlSessionMethodVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );
  }

}
