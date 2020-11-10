package vom.client.bci.mybatis;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class MybatisDefaultSqlSessionMethodVisitorTest {

  private final static String target = "org/apache/ibatis/session/defaults/DefaultSqlSession";

  @Test
  void shouldTastingSoGood() throws IOException {
    final byte[] classfileBuffer = classfileBytes(target);
    final byte[] byteCodes =
      new MybatisDefaultSqlSessionAdapter(classfileBuffer, target).toBytes();

    writeTastingClassfile(byteCodes);

  }

}
