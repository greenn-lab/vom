package vom.client.bci.jdbc.visitor;

import org.junit.jupiter.api.Test;
import vom.client.bci.jdbc.PreparedStatementAdapter;

import java.io.IOException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class PreparedStatementParametersVisitorTest {

  private final static String target = "org/hsqldb/jdbc/JDBCPreparedStatement";

  @Test
  void shouldTastingSoGood() throws IOException {
    final byte[] classfileBuffer = classfileBytes(target);
    final byte[] byteCodes =
      new PreparedStatementAdapter(classfileBuffer, target).toBytes();

    writeTastingClassfile(byteCodes);

  }

}
