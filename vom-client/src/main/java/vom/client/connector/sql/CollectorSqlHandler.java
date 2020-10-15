package vom.client.connector.sql;

import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class CollectorSqlHandler extends DefaultHandler {

  @Getter
  private final Map<String, String> queries = new HashMap<String, String>();

  private String id;

  private StringBuffer sql = new StringBuffer();


  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if ("sql".equals(qName)) {
      id = attributes.getValue("id");
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("sql".equals(qName)) {
      queries.put(id, sql.toString());
      sql.setLength(0);
    }
  }

  @Override
  public void characters(char[] ch, int start, int length) {
    sql.append(ch, start, length);
  }

}
