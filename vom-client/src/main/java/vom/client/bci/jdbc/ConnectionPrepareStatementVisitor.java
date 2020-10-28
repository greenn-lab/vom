package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.trove.QueryInChasing.QUERY_INTERNAL;
import static vom.client.bci.trove.QueryInChasing.QUERY_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class ConnectionPrepareStatementVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  public ConnectionPrepareStatementVisitor(
    int access,
    String descriptor,
    MethodVisitor visitor
  ) {
    super(ASM_VERSION, access, descriptor, visitor);
  }

  @Override
  @SuppressWarnings("DuplicatedCode")
  public void visitCode() {
    // new QueryInChasing()
    mv.visitTypeInsn(NEW, QUERY_INTERNAL);
    mv.visitInsn(DUP);

    // QueryInChasing's 1st parameter
    mv.visitVarInsn(ALOAD, 1);

    mv.visitMethodInsn(
      INVOKESPECIAL,
      QUERY_INTERNAL,
      CONSTRUCTOR,
      VOID_STRING,
      false);

    final int varChase = newLocal(QUERY_TYPE);
    mv.visitVarInsn(ASTORE, varChase);
    mv.visitVarInsn(ALOAD, varChase);

    Trover.chase(mv);

    mv.visitCode();
  }

}
