package vom.client.bci.tasting.try_catch;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.OBJECT_INTERNAL;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class TastingTryCatchTest implements Opcodes {

  private static final String CLASS_NAME = "tasting/TastingTryCatch";
  private static final String METHOD_NAME = "hi";
  private static final String METHOD_DESC = "(I)J";

  @Test
  void shouldRunBCI() throws IOException {
    final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    final ClassVisitor visitor = new ClassVisitor(ASM7, writer) {
      @Override
      public MethodVisitor visitMethod(
        int access, String name, String descriptor, String signature,
        String[] exceptions
      ) {
        final MethodVisitor methodVisitor =
          super.visitMethod(access, name, descriptor, signature, exceptions);

        if (METHOD_NAME.equals(name)) {
          return new TastingTryCatchMethodVisitor(methodVisitor);
        }

        return methodVisitor;
      }
    };

    visitor.visit(
      V1_8, ACC_PUBLIC, CLASS_NAME, null,
      OBJECT_INTERNAL, null
    );

    visitor.visitMethod(
      ACC_PUBLIC, METHOD_NAME, METHOD_DESC, null, null
    )
      .visitCode();

    writeTastingClassfile(writer);
  }

}
