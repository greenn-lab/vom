package vom.client.fastjson;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.lang.reflect.Type;

public class ArgumentValueCodec implements ObjectSerializer {

  @Override
  public void write(
    JSONSerializer jsonSerializer,
    Object object,
    Object fieldName,
    Type fieldType,
    int features
  ) {
    if (null == object) {
      jsonSerializer.writeNull();
    }
    else {
      jsonSerializer.write(object.toString());
    }
  }

}
