package vom.client.bci.trove;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class TroveSerializationTest {

  @Test
  void whyOccurredException() {
    final Trove trove = new Trove("X", "Y", "Z", "A");
    final SQLChaser booty = new SQLChaser("SELECT 1 FROM DUAL");
    booty.addArgument(0, new Trove.Argument(new String[]{"A", "b"}));
    trove.addBooty(booty);


    Map<String, String> pm = new HashMap<String, String>();
    pm.put("test", Arrays.toString(new String[]{"a", "b"}));
    trove.setParameters(pm);

    final String serialize = JSON.toJSONString(trove);

    System.out.println(serialize);
  }

}
