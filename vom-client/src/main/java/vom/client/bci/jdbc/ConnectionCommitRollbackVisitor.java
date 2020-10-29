package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.TroveExecutor;

import static vom.client.bci.trove.SQLChaser.SQL_CHASER_INTERNAL;
import static vom.client.bci.trove.SQLChaser.SQL_CHASER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class ConnectionCommitRollbackVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private final String methodName;
  private int varChase;


  public ConnectionCommitRollbackVisitor(
    int access,
    String methodName,
    String descriptor,
    MethodVisitor visitor
  ) {
    super(ASM7, access, descriptor, visitor);
    this.methodName = methodName;
  }


  @Override
  @SuppressWarnings("DuplicatedCode")
  public void visitCode() {
    // new SQLChaser(...)
    mv.visitTypeInsn(NEW, SQL_CHASER_INTERNAL);
    mv.visitInsn(DUP);

    // SQLChaser's 1st parameter
    mv.visitLdcInsn(methodName);

    mv.visitMethodInsn(
      INVOKESPECIAL,
      SQL_CHASER_INTERNAL,
      CONSTRUCTOR,
      VOID_STRING,
      false);

    varChase = newLocal(SQL_CHASER_TYPE);
    mv.visitVarInsn(ASTORE, varChase);
    mv.visitVarInsn(ALOAD, varChase);

    TroveExecutor.chase(mv);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if (IRETURN <= opcode && RETURN >= opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      TroveExecutor.close(mv);
    }

    mv.visitInsn(opcode);
  }

}
