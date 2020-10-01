package vom.client.asm.web.servlet;

import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.Config;
import vom.client.chase.Booty;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;

@Slf4j
public class HttpServletServiceMethodVisitor extends LocalVariablesSorter {
  
  private static final String CLASS_INTERNAL_NAME =
      Type.getInternalName(HttpServletServiceMethodVisitor.class);
  
  public HttpServletServiceMethodVisitor(MethodVisitor visitor, int access, String descriptor) {
    super(Config.ASM_VERSION, access, descriptor, visitor);
  }
  
  @Override
  public void visitCode() {
    mv.visitVarInsn(Opcodes.ALOAD, 1);
    mv.visitMethodInsn(
        Opcodes.INVOKESTATIC,
        CLASS_INTERNAL_NAME,
        "swipe",
        "(Ljavax/servlet/http/HttpServletRequest;)V",
        false);
    
    mv.visitCode();
  }
  
  @Override
  public void visitInsn(int opcode) {
    if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
      mv.visitMethodInsn(
          Opcodes.INVOKESTATIC,
          CLASS_INTERNAL_NAME,
          "emit",
          "()V",
          false);
    }
    
    mv.visitInsn(opcode);
  }
  
  public static void swipe(HttpServletRequest request) {
    final Booty booty = Booty.builder()
        .id(Config.getId())
        .collected(System.currentTimeMillis())
        .uri(request.getRequestURI())
        .parameters(new HashMap<String, String>(request.getParameterMap()))
        .build();
    
    @SuppressWarnings("unchecked") final Enumeration<String> headerNames =
        request.getHeaderNames();
    
    while (headerNames.hasMoreElements()) {
      final String name = headerNames.nextElement();
      booty.addHeader(name, request.getHeader(name));
    }
    
    System.err.printf("swipe(request): %d%n", booty.getCollected());
    HttpServletServiceAdapter.BOOTY.set(booty);
  }
  
  public static void emit() {
    final Booty builder = HttpServletServiceAdapter.BOOTY.get();
    if (builder != null) {
      System.err.printf("emit(): %d%n", builder.getCollected());
      HttpServletServiceAdapter.BOOTY.remove();
    }
  }
  
  public static void addMethodStack(String signature, long elapsed) {
    final Booty builder = HttpServletServiceAdapter.BOOTY.get();
    if (builder != null) {
      builder.addTrove(
          signature,
          "",
          builder.getCollected() - elapsed
      );
    }
  }
  
}
