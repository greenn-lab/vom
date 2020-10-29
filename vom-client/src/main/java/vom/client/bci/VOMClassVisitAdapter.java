package vom.client.bci;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public abstract class VOMClassVisitAdapter
  extends ClassVisitor
  implements ClassWritable, Opcodes {

  protected final byte[] buffer;
  protected final String className;


  public VOMClassVisitAdapter(byte[] buffer, String className) {
    super(ASM7);

    this.buffer = buffer;
    this.className = className;
  }


  @Override
  public byte[] toBytes() {
    final ClassReader reader = new ClassReader(buffer);
    final ClassWriter writer = new ClassWriter(
      reader,
      ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS
    );

    super.cv = writer;

    reader.accept(this, ClassReader.EXPAND_FRAMES);

    return writer.toByteArray();
  }

}
