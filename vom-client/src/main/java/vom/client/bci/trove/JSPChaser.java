package vom.client.bci.trove;

import lombok.Getter;
import org.objectweb.asm.Type;

import java.io.Serializable;

@Getter
public class JSPChaser extends Chaser implements Serializable {

  public static final Type JSP_CHASER_TYPE = Type.getType(JSPChaser.class);
  public static final String JSP_CHASER_INTERNAL =
    Type.getInternalName(JSPChaser.class);

  private final String jsp;


  public JSPChaser(String jsp) {
    this.jsp = jsp;
  }

  @Override
  public String signature() {
    return jsp;
  }
}
