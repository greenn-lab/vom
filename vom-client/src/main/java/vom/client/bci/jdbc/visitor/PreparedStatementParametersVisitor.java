package vom.client.bci.jdbc.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.bci.AbstractMethodVisitor;
import vom.client.bci.trove.TroveExecutor;
import vom.client.bci.utility.OpcodeUtils;
import vom.client.bci.utility.PrimitiveTypes;

public class PreparedStatementParametersVisitor
  extends AbstractMethodVisitor {

  private final Type valueType;


  public PreparedStatementParametersVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);

    final Type[] argumentTypes = Type.getArgumentTypes(descriptor);
    this.valueType = argumentTypes.length > 1 ? argumentTypes[1] : null;

  }

  @Override
  public void visitCode() {
    mv.visitCode();

    if (valueType == null) return;

    // Trover.glean()'s 1st parameter
    mv.visitVarInsn(ILOAD, 1);

    // Trover.glean()'s 2nd parameter
    mv.visitVarInsn(
      OpcodeUtils.loadLocalVariable(valueType),
      2
    );
    PrimitiveTypes.valueOf(valueType, mv);

    TroveExecutor.glean(mv);
  }

}
