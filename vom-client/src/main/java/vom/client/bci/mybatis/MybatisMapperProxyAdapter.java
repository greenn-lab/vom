package vom.client.bci.mybatis;

import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMClassVisitAdapter;

public class MybatisMapperProxyAdapter extends VOMClassVisitAdapter {

  private static final String INTERNAL_NAME =
    "org/apache/ibatis/binding/MapperProxy";
  private static final String METHOD_NAME = "invoke";
  private static final String METHOD_DESC =
    "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)" +
      "Ljava/lang/Object;";


  public MybatisMapperProxyAdapter(byte[] buffer, String className) {
    super(buffer, className);
  }


  @Override
  public boolean isAdaptable() {
    return INTERNAL_NAME.equals(className);
  }

  @Override
  public boolean methodMatches(int access, String methodName, String descriptor) {
    return METHOD_NAME.equals(methodName) &&
      METHOD_DESC.equals(descriptor);
  }

  @Override
  public MethodVisitor methodVisitor(MethodVisitor visitor, String methodName, String descriptor) {
    return new MybatisMapperProxyInvokeVisitor(
      reader,
      visitor,
      methodName,
      descriptor
    );
  }

}
