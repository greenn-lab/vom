package vom.client.bci.trove;

import lombok.Getter;
import org.objectweb.asm.Type;

import java.io.Serializable;

@Getter
public class PaperInChasing extends Chasing implements Serializable {

  public static final Type PAPER_TYPE = Type.getType(PaperInChasing.class);
  public static final String PAPER_INTERNAL =
    Type.getInternalName(PaperInChasing.class);

  private final String jsp;


  public PaperInChasing(String jsp) {
    this.jsp = jsp;
  }

  @Override
  public String signature() {
    return jsp;
  }
}
