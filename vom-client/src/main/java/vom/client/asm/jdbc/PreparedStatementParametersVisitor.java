package vom.client.asm.jdbc;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.asm.jdbc.trove.DBTrove;
import vom.client.asm.utility.OpcodeUtils;
import vom.client.asm.utility.PrimitiveTypes;

import java.io.PrintStream;

import static vom.client.asm.VOMClientTransformer.ASM_VERSION;

public class PreparedStatementParametersVisitor
  extends LocalVariablesSorter
  implements Opcodes {

  private final Type valueType;

  private final String name;
  private String descriptor;

  public PreparedStatementParametersVisitor(
    MethodVisitor visitor,
    int access, String name, String descriptor
  ) {
    super(ASM_VERSION, access, descriptor, visitor);

    this.valueType = Type.getArgumentTypes(descriptor)[1];

    this.name = name;
    this.descriptor = descriptor;
  }

  @Override
  public void visitCode() {
    mv.visitCode();

    final int opcode = OpcodeUtils.loadLocalVariable(valueType);
    mv.visitVarInsn(opcode, 2);
    PrimitiveTypes.valueOf(valueType, mv);

    mv.visitMethodInsn(
      INVOKESTATIC,
      Type.getInternalName(DBTrove.class),
      "addParameter",
      "(Ljava/lang/Object;)V",
      false
    );
  }

}
