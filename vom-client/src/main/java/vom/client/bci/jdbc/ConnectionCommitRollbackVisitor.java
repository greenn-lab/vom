package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;
import static vom.client.bci.trove.QueryInChasing.QUERY_CONSTRUCTOR_DESC;
import static vom.client.bci.trove.QueryInChasing.QUERY_INTERNAL;
import static vom.client.bci.trove.QueryInChasing.QUERY_TYPE;
import static vom.client.bci.utility.OpcodeUtils.CONSTRUCTOR;

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
    super(ASM_VERSION, access, descriptor, visitor);
    this.methodName = methodName;
  }


  @Override
  @SuppressWarnings("DuplicatedCode")
  public void visitCode() {
    // new QueryInChasing()
    mv.visitTypeInsn(NEW, QUERY_INTERNAL);
    mv.visitInsn(DUP);

    // QueryInChasing's 1st parameter
    mv.visitLdcInsn(methodName);

    mv.visitMethodInsn(
      INVOKESPECIAL,
      QUERY_INTERNAL,
      CONSTRUCTOR,
      QUERY_CONSTRUCTOR_DESC,
      false);

    varChase = newLocal(QUERY_TYPE);
    mv.visitVarInsn(ASTORE, varChase);

    mv.visitVarInsn(ALOAD, varChase);
    Trover.chase(mv);

    mv.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    if ((IRETURN <= opcode && RETURN >= opcode) || ATHROW == opcode) {
      mv.visitVarInsn(ALOAD, varChase);
      Trover.bring(mv);
    }

    mv.visitInsn(opcode);
  }

}
