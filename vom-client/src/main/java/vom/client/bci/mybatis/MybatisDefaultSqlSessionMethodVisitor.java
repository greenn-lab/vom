package vom.client.bci.mybatis;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.bci.trove.MethodChaserVisitHelper;

import static vom.client.bci.servlet.visitor.MethodChaserVisitor.OBJECT_ARRAY_TYPE;
import static vom.client.bci.utility.OpcodeUtils.OBJECT_NAME;

public class MybatisDefaultSqlSessionMethodVisitor extends MethodChaserVisitHelper {

  private static final String INTERNAL_NAME =
    Type.getInternalName(MybatisDefaultSqlSessionMethodVisitor.class);
  private static final Type SLICE_NAME_TYPE = Type.getType(String[].class);
  private static final String METHOD_NAME = "sliceName";
  private static final String METHOD_DESC =
    "(Ljava/lang/String;)[Ljava/lang/String;";

  public MybatisDefaultSqlSessionMethodVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
  }

  @Override
  protected void setMethodChaserArguments(MethodVisitor mv) {
    mv.visitInsn(POP2);

    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      METHOD_NAME,
      METHOD_DESC,
      false
    );

    final int varSliceName = super.newLocal(SLICE_NAME_TYPE);
    mv.visitVarInsn(ASTORE, varSliceName);

    // MethodChaser's 1st parameter
    mv.visitVarInsn(ALOAD, varSliceName);
    mv.visitInsn(ICONST_0);
    mv.visitInsn(AALOAD);

    // MethodChaser's 2nd parameter
    mv.visitVarInsn(ALOAD, varSliceName);
    mv.visitInsn(ICONST_1);
    mv.visitInsn(AALOAD);

    final int varParameter = super.newLocal(OBJECT_ARRAY_TYPE);
    mv.visitInsn(ICONST_1);
    mv.visitTypeInsn(ANEWARRAY, OBJECT_NAME);
    mv.visitVarInsn(ASTORE, varParameter);

    mv.visitVarInsn(ALOAD, varParameter);
    mv.visitInsn(ICONST_0);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitInsn(AASTORE);

    mv.visitVarInsn(ALOAD, varParameter);
  }

  public static String[] sliceName(String name) {
    final int i = name.lastIndexOf('.');

    if (i == -1 || i == name.indexOf('.')) {
      return new String[]{"", name};
    }

    return new String[]{
      name.substring(0, i).replace('.', '/'),
      name.substring(i + 1)
    };
  }


}
