package vom.client.connector.sql;

import vom.client.exception.FallDownException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

public class SqlManager extends HashMap<String, String> {

  private static final SqlManager instance = new SqlManager();


  private SqlManager() {
    InputStream in;

    try {
      in = ClassLoader.getSystemResourceAsStream("collector-sql.xml");

      final SAXParserFactory factory = SAXParserFactory.newInstance();
      final SAXParser parser = factory.newSAXParser();

      final CollectorSqlHandler handler = new CollectorSqlHandler();
      parser.parse(in, handler);

      putAll(handler.getQueries());
    }
    catch (Exception e) {
      throw new FallDownException(e /*"Fail to load system resources!"*/);
    }

  }

  public static SqlManager getInstance() {
    return instance;
  }

}
