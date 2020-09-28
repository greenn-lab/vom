package vom.client.asm.web.chaser;

import org.objectweb.asm.Type;

import java.util.Arrays;

public class HttpServletChaser {
  
  protected static final String INTERNAL_NAME =
      Type.getInternalName(HttpServletChaser.class);
  
  public static void seize(String signature, Object[] parameters) {
    System.out.printf("seize [%s] : %s", signature, Arrays.toString(parameters));
  }
}
