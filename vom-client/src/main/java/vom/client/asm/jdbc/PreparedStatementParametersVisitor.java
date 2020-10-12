package vom.client.asm.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.asm.utility.OpcodeUtils;
import vom.client.asm.utility.PrimitiveTypes;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_ADD_PARAM;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_ADD_PARAM_DESC;
import static vom.client.asm.jdbc.trove.DBTrove.DB_TROVE_INTERNAL_NAME;

public class PreparedStatementParametersVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private final Type valueType;


  public PreparedStatementParametersVisitor(
    MethodVisitor visitor,
    int access, String descriptor
  ) {
    super(ASM_VERSION, access, descriptor, visitor);

    this.valueType = Type.getArgumentTypes(descriptor)[1];
  }

  @Override
  public void visitCode() {
    mv.visitCode();

    final int opcode = OpcodeUtils.loadLocalVariable(valueType);
    mv.visitVarInsn(opcode, 2);
    PrimitiveTypes.valueOf(valueType, mv);

    mv.visitMethodInsn(
      INVOKESTATIC,
      DB_TROVE_INTERNAL_NAME,
      DB_TROVE_ADD_PARAM,
      DB_TROVE_ADD_PARAM_DESC,
      false
    );
  }

}
