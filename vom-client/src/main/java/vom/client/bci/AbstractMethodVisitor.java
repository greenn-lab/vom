package vom.client.bci;

import lombok.Setter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

@Setter
public class AbstractMethodVisitor extends LocalVariablesSorter
  implements Opcodes {

  protected String className;
  protected String methodName;


  public AbstractMethodVisitor(
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
