package vom.client.asm.web.chaser;

import vom.client.Config;
import vom.client.asm.AgentLinkageAdapter;
import vom.client.asm.utility.InvokeStatic;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.Arrays;

import static org.objectweb.asm.ClassReader.EXPAND_FRAMES;

public class HttpServletChaserAdapter extends ClassVisitor implements AgentLinkageAdapter, Opcodes {
  
  private static final String INTERNAL_NAME =
      Type.getInternalName(HttpServletChaserAdapter.class);
  
  private String className;
  private boolean isInterface;
  
  
  public HttpServletChaserAdapter(byte[] classfileBuffer) {
    super(Config.ASM_VERSION);
    
    final ClassReader reader = new ClassReader(classfileBuffer);
    this.cv = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    
    reader.accept(this, EXPAND_FRAMES);
  }
  
  @Override
  public byte[] toByteArray() {
    return ((ClassWriter) cv).toByteArray();
  }
  
  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    cv.visit(version, access, name, signature, superName, interfaces);
    
    className = name;
    isInterface = (access & ACC_INTERFACE) != 0;
  }
  
  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor visitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
    
    if (!isInterface
        && !"<init>".equals(name)
        && null != visitor
        && Opcodes.ACC_PUBLIC == access) {
      visitor = new ChaserTargetMethodVisitor(
          visitor,
          access,
          className,
          name,
          descriptor
      );
    }
    
    return visitor;
  }
  
  public static void swipe(String classAndMethod, Object[] parameters) {
    System.out.printf(
        "%s.swipe(%n\\tclassAndMethod: %s,%n\\tparameters: %s%n)%n",
        HttpServletChaserAdapter.class.getName(),
        classAndMethod,
        Arrays.toString(parameters)
    );
  }
  
  
  private static class ChaserTargetMethodVisitor extends LocalVariablesSorter implements Opcodes {
    
    private final String classAndMethod;
    private final Type[] arguments;
    
    public ChaserTargetMethodVisitor(MethodVisitor visitor, int access, String className, String methodName, String descriptor) {
      super(Config.ASM_VERSION, access, descriptor, visitor);
      
      this.classAndMethod = className + "#" + methodName;
      this.arguments = Type.getArgumentTypes(descriptor);
    }
    
    @Override
    public void visitCode() {
      invokeMethodSwipe(
          mv,
          classAndMethod,
          arguments,
          newLocal(Type.getType(Object[].class))
      );
      
      mv.visitCode();
    }
    
    @Override
    public void visitInsn(int opcode) {
      mv.visitInsn(opcode);
      
      if (RETURN >= opcode && IRETURN <= opcode) {
        mv.visitMaxs(0, 0);
        mv.visitEnd();
      }
    }
    
    private void invokeMethodSwipe(MethodVisitor mv, String classAndMethod, Type[] arguments, int arrayIndex) {
      mv.visitLdcInsn(classAndMethod);
      
      if (0 == arguments.length) {
        mv.visitInsn(ACONST_NULL);
      }
      else {
        mv.visitIntInsn(BIPUSH, arguments.length);
        mv.visitTypeInsn(ANEWARRAY, TYPE_OBJECT);
        mv.visitVarInsn(ASTORE, arrayIndex);
  
        int index = 1;
        int arrayPoint = 0;
        for (final Type argument : arguments) {
          mv.visitVarInsn(ALOAD, arrayIndex);
          mv.visitIntInsn(BIPUSH, arrayPoint++);
          
          switch (argument.getSort()) {
            case Type.BOOLEAN:
              mv.visitVarInsn(ILOAD, index);
              InvokeStatic.booleanValueOf(mv);
              break;
            case Type.CHAR:
              mv.visitVarInsn(ILOAD, index);
              InvokeStatic.characterValueOf(mv);
              break;
            case Type.BYTE:
              mv.visitVarInsn(ILOAD, index);
              InvokeStatic.byteValueOf(mv);
              break;
            case Type.SHORT:
              mv.visitVarInsn(ILOAD, index);
              InvokeStatic.shortValueOf(mv);
              break;
            case Type.INT:
              mv.visitVarInsn(ILOAD, index);
              InvokeStatic.integerValueOf(mv);
              break;
            case Type.FLOAT:
              mv.visitVarInsn(FLOAD, index);
              InvokeStatic.floatValueOf(mv);
              break;
            case Type.LONG:
              mv.visitVarInsn(LLOAD, index);
              InvokeStatic.longValueOf(mv);
              break;
            case Type.DOUBLE:
              mv.visitVarInsn(DLOAD, index);
              InvokeStatic.doubleValueOf(mv);
              break;
            default:
              mv.visitVarInsn(ALOAD, index);
          }

          mv.visitInsn(AASTORE);
         
          index += argument.getSize();
        }
        
        mv.visitVarInsn(ALOAD, arrayIndex);
      }
      
      mv.visitMethodInsn(
          INVOKESTATIC,
          INTERNAL_NAME,
          "swipe",
          "(Ljava/lang/String;[Ljava/lang/Object;)V",
          false
      );
    }
    
  }
  
}
