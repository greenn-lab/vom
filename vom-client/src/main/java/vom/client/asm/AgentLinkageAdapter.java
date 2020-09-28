package vom.client.asm;

import org.objectweb.asm.Type;

public interface AgentLinkageAdapter {
  
  String TYPE_OBJECT = Type.getInternalName(Object.class);
  
  byte[] toByteArray();
  
}
