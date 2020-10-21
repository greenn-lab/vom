package vom.client.bci.tasting.try_catch;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM7;

public class TastingTryCatchClassVisitor extends ClassVisitor {

  public TastingTryCatchClassVisitor(ClassVisitor classVisitor) {
    super(ASM7, classVisitor);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    final MethodVisitor visitor = super.visitMethod(access, name, descriptor, signature, exceptions);

    System.out.println(name + descriptor);
    if ("<init>".equals(name)) {
      return new TastingTryCatchMethodVisitor(visitor);
    }

    return visitor;
  }

}
