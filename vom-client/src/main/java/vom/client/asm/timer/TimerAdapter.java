package vom.client.asm.timer;

import vom.client.Config;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class TimerAdapter extends ClassVisitor implements Opcodes {
  
  private String className;
  private boolean isInterface;
  
  public TimerAdapter(ClassVisitor cv) {
    super(Config.ASM_VERSION, cv);
  }
  
  @Override
  public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    cv.visit(version, access, name, signature, superName, interfaces);
    className = name;
    isInterface = (access & ACC_INTERFACE) != 0;
  }
  
  @Override
  public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
    if (!isInterface && mv != null && !"<init>".equals(name)) {
      mv = new TimerMethodVisitor(mv, access, descriptor);
    }
    return mv;
  }
  
  
  private class TimerMethodVisitor extends LocalVariablesSorter implements Opcodes {
    
    public TimerMethodVisitor(MethodVisitor mv, int access, String descriptor) {
      super(Config.ASM_VERSION, access, descriptor, mv);
    }
    
    @Override
    public void visitCode() {
      mv.visitCode();
      mv.visitFieldInsn(GETSTATIC, className, "timer", "J");
      mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
      mv.visitInsn(LSUB);
      mv.visitFieldInsn(PUTSTATIC, className, "timer", "J");
    }
    
    @Override
    public void visitInsn(int opcode) {
      if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
        mv.visitFieldInsn(GETSTATIC, className, "timer", "J");
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        mv.visitInsn(LADD);
        mv.visitFieldInsn(PUTSTATIC, className, "timer", "J");
      }
      mv.visitInsn(opcode);
    }
    
  }
  
}
