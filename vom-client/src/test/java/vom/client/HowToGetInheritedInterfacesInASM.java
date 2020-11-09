package vom.client;

import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import vom.client.performance.SystemPerformanceWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;

class HowToGetInheritedInterfacesInASM {

  @Test
  void shouldGetInterfaces() {
    final byte[] bytes = classfileBytes(ClassReaderTest.class.getName());

    final ClassReader reader = new ClassReader(bytes);
    final List<String> interfaces = getInterfaces(reader);

    assert interfaces.size() > 0;

    for (String interface_ : interfaces) {
      System.out.println(interface_);
    }
  }

  List<String> getInterfaces(ClassReader reader) {
    List<String> interfaces = new ArrayList<String>();
    System.out.println(">>" + reader.getClassName());

    for (String interface_ : reader.getInterfaces()) {
      interfaces.add(interface_);
      interfaces.addAll(getInterfaces(new ClassReader(classfileBytes(interface_))));
    }

    if (null != reader.getSuperName()) {
      final ClassReader superReader = new ClassReader(classfileBytes(reader.getSuperName()));
      interfaces.addAll(getInterfaces(superReader));
    }

    return interfaces;
  }


}
