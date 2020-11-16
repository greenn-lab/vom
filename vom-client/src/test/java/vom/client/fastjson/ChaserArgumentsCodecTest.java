package vom.client.fastjson;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
import vom.client.bci.trove.SQLChaser;
import vom.client.bci.trove.Trove;

class ChaserArgumentsCodecTest {

  @Test
  void shouldWriteToJson() {
    Trove trove = new Trove();

    final SQLChaser sql = new SQLChaser("hello sql");
    sql.addArgument(0, "test");
    sql.addArgument(1, "check");
    sql.addArgument(2, "confirm");
    trove.addBooty(sql);


    final String s = JSON.toJSONString(trove);

    System.out.println(s);

  }

}
