package serialize;

import org.h2.Driver;
import org.junit.jupiter.api.Test;
import org.nustaq.serialization.FSTConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class SerializeResearch {
  
  @Test
  void shouldSerialize() throws SQLException {
    Driver driver = new Driver();
    final Connection connection = driver.connect("jdbc:h2:mem:test", null);
    final PreparedStatement ps = connection.prepareStatement("select current_date() from dual");
    final ResultSet rs = ps.executeQuery();
    
//    CachedRowSet cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
//    cachedRowSet.populate(rs);
//    rs.close();
    
    final FSTConfiguration mb = FSTConfiguration.createDefaultConfiguration().setForceSerializable(true);
    final byte[] bytes = mb.asByteArray(rs);
    
    final ResultSet trs = (ResultSet) mb.asObject(bytes);
    
    if (trs.next()) {
      do {
        final Object object = trs.getObject(1);
        System.out.println(object + "/" + object.getClass());
      }
      while (trs.next());
    }
  }
  
}
