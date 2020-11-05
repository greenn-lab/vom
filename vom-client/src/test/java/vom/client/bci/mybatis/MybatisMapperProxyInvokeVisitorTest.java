package vom.client.bci.mybatis;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class MybatisMapperProxyInvokeVisitorTest {

  private final static String target = "org/apache/ibatis/binding/MapperProxy";

  @Test
  void shouldTastingSoGood() throws IOException {
    final byte[] classfileBuffer = classfileBytes(target);
    final byte[] byteCodes =
      new MybatisMapperProxyAdapter(classfileBuffer, target).toBytes();

    writeTastingClassfile(byteCodes);

  }

}
