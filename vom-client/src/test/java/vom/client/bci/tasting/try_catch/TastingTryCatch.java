package vom.client.bci.tasting.try_catch;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;

import static vom.client.bci.tasting.TastingUtils.writeTastingClassfile;

public class TastingTryCatch implements Opcodes {

  public static void main(String[] args) throws IOException {
    final String className = "example/HelloASM";
    final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    final TastingTryCatchClassVisitor visitor =
      new TastingTryCatchClassVisitor(writer);

    visitor.visit(
      V1_8,
      ACC_PUBLIC,
      className,
      null,
      Type.getInternalName(Object.class),
      null
    );

    MethodVisitor mv =
      visitor.visitMethod(
        ACC_PUBLIC,
        "<init>",
        "(I)V",
        null,
        null
      );

    mv.visitCode();

    writeTastingClassfile(writer);
  }

}
