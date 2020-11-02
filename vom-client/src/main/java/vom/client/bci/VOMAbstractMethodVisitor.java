package vom.client.bci;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class VOMAbstractMethodVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  protected final String className;
  protected final String methodName;


  public VOMAbstractMethodVisitor(
    ClassReader reader,
    MethodVisitor visitor,
    String methodName,
    String descriptor
  ) {
    super(ASM7, reader.getAccess(), descriptor, visitor);

    this.className = reader.getClassName();
    this.methodName = methodName;
  }

}
