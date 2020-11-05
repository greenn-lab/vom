package vom.client.bci.mybatis;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.bci.trove.MethodChaserVisitHelper;

import java.lang.reflect.Method;

public class MybatisMapperProxyInvokeVisitor extends MethodChaserVisitHelper {

  private static final String INTERNAL_NAME =
    Type.getInternalName(MybatisMapperProxyInvokeVisitor.class);
  private static final Type METHOD_TYPE = Type.getType(String[].class);
  private static final String METHOD_NAME = "getName";
  private static final String METHOD_DESC =
    "(Ljava/lang/reflect/Method;)[Ljava/lang/String;";


  public MybatisMapperProxyInvokeVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
  }

  @Override
  protected void setMethodChaserArguments(MethodVisitor mv) {
    // remove 2 element(className, methodName) in operand stack
    mv.visitInsn(POP2);

    mv.visitVarInsn(ALOAD, 2);
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      METHOD_NAME,
      METHOD_DESC,
      false
    );

    final int varNames = super.newLocal(METHOD_TYPE);
    mv.visitVarInsn(ASTORE, varNames);

    // MethodChaser's 1st parameter
    mv.visitVarInsn(ALOAD, varNames);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(AALOAD);

    // MethodChaser's 2nd parameter
    mv.visitVarInsn(ALOAD, varNames);
    mv.visitInsn(ICONST_1);
    mv.visitInsn(AALOAD);

    // MethodChaser's 3rd parameter
    mv.visitVarInsn(ALOAD, 3);
  }


  public static String[] getName(Method method) {
    return new String[]{
      Type.getInternalName(method.getDeclaringClass()),
      method.getName()
    };
  }

}
