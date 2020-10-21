package vom.client.bci.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.Trover;
import vom.client.bci.utility.OpcodeUtils;
import vom.client.bci.utility.PrimitiveTypes;

import static vom.client.bci.VOMClientTransformer.ASM_VERSION;

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

    OpcodeUtils.print(mv, descriptor);
  }

  @Override
  public void visitCode() {
    mv.visitCode();

    // Trover.glean()'s 1st parameter
    mv.visitVarInsn(
      OpcodeUtils.loadLocalVariable(valueType),
      2
    );
    PrimitiveTypes.valueOf(valueType, mv);

    Trover.glean(mv);
  }

}
