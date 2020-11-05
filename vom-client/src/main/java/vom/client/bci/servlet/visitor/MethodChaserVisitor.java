package vom.client.bci.servlet.visitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.bci.trove.MethodChaserVisitHelper;

import static vom.client.bci.utility.OpcodeUtils.argumentsToObjectArray;

public class MethodChaserVisitor extends MethodChaserVisitHelper {

  private static final Type OBJECT_ARRAY_TYPE = Type.getType(Object[].class);

  private final Type[] argumentTypes;

  public MethodChaserVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(reader, visitor, methodName, descriptor);
    this.argumentTypes = Type.getArgumentTypes(descriptor);
  }

  @Override
  protected void setMethodChaserArguments(MethodVisitor mv) {
    argumentsToObjectArray(
      mv,
      argumentTypes,
      newLocal(OBJECT_ARRAY_TYPE)
    );

  }

}
