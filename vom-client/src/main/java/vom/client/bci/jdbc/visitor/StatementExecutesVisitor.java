package vom.client.bci.jdbc.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import vom.client.bci.VOMAbstractMethodVisitor;
import vom.client.bci.trove.TroveExecutor;

import static vom.client.bci.trove.SQLChaser.SQL_CHASER_INTERNAL;
import static vom.client.bci.trove.SQLChaser.SQL_CHASER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class StatementExecutesVisitor
  extends VOMAbstractMethodVisitor {

  private int varChase;


  public StatementExecutesVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
  }


  @Override
  @SuppressWarnings("DuplicatedCode")
  public void visitCode() {
    // new SQLChaser(...)
    mv.visitTypeInsn(NEW, SQL_CHASER_INTERNAL);
    mv.visitInsn(DUP);

    // SQLChaser's 1st parameter
    mv.visitVarInsn(ALOAD, 1);

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
  @SuppressWarnings("DuplicatedCode")
  public void visitInsn(int opcode) {
    mv.visitInsn(opcode);

    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      TroveExecutor.close(mv);
    }
  }

}
