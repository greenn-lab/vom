package vom.client.asm.web.servlet;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import vom.client.Booty;
import vom.client.Config;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

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
    final Booty.Builder builder = Booty.newBuilder();
    HttpServletServiceAdapter.BOOTY.set(builder);
    
    builder.setId(Config.getId())
        .setTimestamp(System.currentTimeMillis())
        .setUri(request.getRequestURI());
    
    fillParametersAndHeaders(request, builder);
    
    System.err.printf("swipe(request): %d%n", builder.getTimestamp());
  }
  
  public static void emit() {
    final Booty.Builder builder = HttpServletServiceAdapter.BOOTY.get();
    if (builder != null) {
      System.err.printf("emit(): %d%n", builder.getTimestamp());
      HttpServletServiceAdapter.BOOTY.remove();
    }
  }
  
  public static void addMethodStack(String signature, long elapsed) {
    final Booty.Builder builder = HttpServletServiceAdapter.BOOTY.get();
    if (builder != null) {
      builder.addTroves(
          Booty.Trove.newBuilder()
              .setSignature(signature)
              .setElapsed((int) (builder.getTimestamp() - elapsed)).build()
      );
    }
  }
  
  private static void fillParametersAndHeaders(HttpServletRequest request, Booty.Builder builder) {
    @SuppressWarnings("unchecked") final Map<String, String> parameters = request.getParameterMap();
    builder.putAllParameter(parameters);
    
    @SuppressWarnings("unchecked") final Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      final String headerName = headerNames.nextElement();
      builder.putHeader(headerName, request.getHeader(headerName));
    }
  }
  
}
