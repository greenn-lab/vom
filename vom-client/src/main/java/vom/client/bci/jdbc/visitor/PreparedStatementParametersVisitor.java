package vom.client.bci.jdbc.visitor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;
import vom.client.bci.utility.PrimitiveTypes;

public class PreparedStatementParametersVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private final Type valueType;


  public PreparedStatementParametersVisitor(
    MethodVisitor visitor,
    int access, String descriptor
  ) {
    super(ASM7, access, descriptor, visitor);

    final Type[] argumentTypes = Type.getArgumentTypes(descriptor);
    this.valueType = argumentTypes.length > 1 ? argumentTypes[1] : null;
  }

  @Override
  public void visitCode() {
    mv.visitCode();

    if (valueType == null) return;

    // Trover.glean()'s 1st parameter
    mv.visitVarInsn(
      OpcodeUtils.loadLocalVariable(valueType),
      2
    );
    PrimitiveTypes.valueOf(valueType, mv);

    TroveExecutor.glean(mv);
  }

}
