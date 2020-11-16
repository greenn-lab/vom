package vom.client.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import vom.client.bci.trove.Trove.Argument;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChaserArgumentsCodec implements ObjectSerializer {

  @Override
  public void write(
    JSONSerializer jsonSerializer,
    Object object,
    Object fieldName,
    Type fieldType,
    int features
  ) {
    @SuppressWarnings("unchecked") final Map<Integer, Argument> map =
      (Map<Integer, Argument>) object;

    final List<Argument> listFromMap = new ArrayList<Argument>(map.size());

    for (Map.Entry<Integer, Argument> entry : map.entrySet()) {
      listFromMap.add(entry.getValue());
    }

    jsonSerializer.write(listFromMap);
  }

}
