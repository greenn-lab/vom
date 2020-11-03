package vom.client.bci.jdbc;

import org.h2.jdbc.JdbcPreparedStatement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import vom.client.Config;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class JdbcConnectionPrepareStatementAdapterTest {

  @BeforeAll
  static void setup() {
    Config.configure();
  }

  @Test
  void shouldTransformPreparedStatementExecutor() throws IOException {
    final String className = Type.getInternalName(JdbcPreparedStatement.class);

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new JdbcPreparedStatementExecutesAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

}
