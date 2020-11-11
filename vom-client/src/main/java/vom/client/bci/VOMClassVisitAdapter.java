package vom.client.bci;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import vom.client.Config;
import vom.client.bci.utility.OpcodeUtils;

import java.io.File;

public abstract class VOMClassVisitAdapter
  extends ClassVisitor
  implements ClassWritable, Opcodes {

  protected final byte[] buffer;
  protected final String className;

  protected ClassReader reader;


  public VOMClassVisitAdapter(byte[] buffer, String className) {
    super(ASM7);

    this.buffer = buffer;
    this.className = className;
  }


  @Override
  public byte[] toBytes() {
    reader = new ClassReader(buffer);

    final ComputeClassWriter writer = new ComputeClassWriter(
      Thread.currentThread().getContextClassLoader(),
      ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
    );

    super.cv = writer;

    reader.accept(this, ClassReader.EXPAND_FRAMES);

    if (Boolean.parseBoolean(Config.get("debug.transform"))) {
      final String out = System.getProperty("user.home")
        + "/.vom/classes/"
        + className.replace('.', '/')
        + ".class";

      //noinspection ResultOfMethodCallIgnored
      new File(new File(out).getParent()).mkdirs();

      OpcodeUtils.writeTastingClassfile(writer.toByteArray(), out);
    }

    return writer.toByteArray();
  }


  @Override
  public MethodVisitor visitMethod(
    int access,
    String name,
    String descriptor,
    String signature,
    String[] exceptions
  ) {
    final MethodVisitor visitor =
      super.visitMethod(access, name, descriptor, signature, exceptions);

    if (visitor != null && methodMatches(access, name, descriptor)) {
      return methodVisitor(visitor, name, descriptor);
    }

    return visitor;
  }

}
