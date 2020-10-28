package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trove;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.trove.SQLChaser.QUERY_INTERNAL;
import static vom.client.bci.trove.SQLChaser.QUERY_TYPE;
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
    super(ASM_VERSION, access, descriptor, visitor);
  }


  @Override
  @SuppressWarnings("DuplicatedCode")
  public void visitCode() {
    // new QueryInChasing(...)
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

    varChase = newLocal(QUERY_TYPE);
    mv.visitVarInsn(ASTORE, varChase);

    mv.visitVarInsn(ALOAD, varChase);
    Trove.chase(mv);

    mv.visitCode();
  }

  @Override
  @SuppressWarnings("DuplicatedCode")
  public void visitInsn(int opcode) {
    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      Trove.bring(mv);
    }

    mv.visitInsn(opcode);
  }

}
