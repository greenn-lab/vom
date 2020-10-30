package vom.client.bci.jdbc;

import oracle.jdbc.driver.OracleDriver;
import org.h2.Driver;
import org.h2.jdbc.JdbcConnection;
import org.h2.jdbc.JdbcPreparedStatement;
import org.h2.jdbc.JdbcStatement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Type;
import vom.client.Config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static vom.client.bci.tasting.BCITastingUtils.classfileBytes;
import static vom.client.bci.tasting.BCITastingUtils.writeTastingClassfile;

class JdbcAdapterTest {

  @BeforeAll
  static void setup() {
    Config.configure();
  }

  @Test
  void shouldTransformConnectionPrepareStatement() throws IOException {
    final String className = Type.getInternalName(JdbcConnection.class);

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new JdbcAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

  @Test
  void shouldTransformPreparedStatementExecutor() throws IOException {
    final String className = Type.getInternalName(JdbcPreparedStatement.class);

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new JdbcAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

  @Test
  void shouldTransformStatementExecutor() throws IOException {
    final String className = Type.getInternalName(JdbcStatement.class);

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new JdbcAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

  @Test
  void shouldTransformOraclePreparedStatementParameters() throws IOException {
    final String className =
      "oracle/jdbc/driver/OraclePreparedStatementWrapper";

    final byte[] classfileBuffer = classfileBytes(className);
    final byte[] byteCodes =
      new JdbcAdapter(classfileBuffer, className).toBytes();

    writeTastingClassfile(byteCodes);
  }

  @Test
  void shouldGetSQLAndParameters() throws SQLException {
    final Driver driver = new Driver();
    final Connection connection = driver.connect("jdbc:h2:mem:test", null);
    final PreparedStatement ps = connection
      .prepareStatement(
        "SELECT current_date() FROM DUAL WHERE 1 = ?1 " +
        "AND 2 = ?2"
      );

    ps.setString(1, "1");
    ps.setInt(2, 2);

    final ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      final Object object = rs.getObject(1);
      System.out.println(object);

      Assertions.assertNotNull(object);
    }
  }

  @Test
  void oracleOK() throws SQLException {
    final OracleDriver driver = new OracleDriver();
    final Connection connection = driver
      .connect("jdbc:oracle:thin:APSAMPLE/APSAMPLE@192.168.0.244:1521:orcl",
        null);

    final PreparedStatement ps = connection
      .prepareStatement(
        "SELECT SYSDATE FROM DUAL WHERE 1 = ? " +
        "AND 2 = ?"
      );

    ps.setString(1, "1");
    ps.setInt(2, 2);

    final ResultSet rs = ps.executeQuery();
    if (rs.next()) {
      final Object object = rs.getObject(1);
      System.out.println(object);

      Assertions.assertNotNull(object);
    }

  }

}
