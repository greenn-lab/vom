package vom.client.bci;

import org.objectweb.asm.MethodVisitor;

public interface ClassWritable {

  boolean isAdaptable();

  byte[] toBytes();

  boolean methodMatches(int access, String methodName, String descriptor);

  MethodVisitor methodVisitor(
    MethodVisitor visitor,
    String methodName,
    String descriptor
  );

}
