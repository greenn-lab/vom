package vom.client.bci.jdbc.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.TroveExecutor;

import static vom.client.bci.trove.SQLChaser.SQL_CHASER_INTERNAL;
import static vom.client.bci.trove.SQLChaser.SQL_CHASER_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;
import static vom.client.bci.utility.OpcodeUtils.VOID_STRING;

public class StatementExecutesVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private int varChase;


  public StatementExecutesVisitor(
    int access,
    String descriptor,
    MethodVisitor visitor
  ) {
    super(ASM7, access, descriptor, visitor);
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
    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      TroveExecutor.close(mv);
    }

    mv.visitInsn(opcode);
  }

}
