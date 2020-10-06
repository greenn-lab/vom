package vom.client.asm.utility;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import vom.client.exception.FallDownException;

import java.lang.reflect.Method;

public class CodeUtils implements Opcodes{
  
  private CodeUtils() {
  }
  
  public static int localVariableOpcode(Type type) {
    switch (type.getSort()) {
      case Type.BOOLEAN:
      case Type.BYTE:
      case Type.CHAR:
      case Type.SHORT:
      case Type.INT:
        return ILOAD;
      case Type.FLOAT:
        return FLOAD;
      case Type.LONG:
        return LLOAD;
      case Type.DOUBLE:
        return DLOAD;
      default:
        return ALOAD;
    }
  }
  
  public static Class<?> typeToClass(Type type) {
    switch (type.getSort()) {
      case Type.BOOLEAN:
        return boolean.class;
      case Type.BYTE:
        return byte.class;
      case Type.CHAR:
        return char.class;
      case Type.SHORT:
        return short.class;
      case Type.INT:
        return int.class;
      case Type.FLOAT:
        return float.class;
      case Type.LONG:
        return long.class;
      case Type.DOUBLE:
        return double.class;
      case Type.ARRAY:
        if (type.getDimensions() == 1
            && !type.getInternalName().contains("/")) {
          return asClass(type.getInternalName());
        }
        else {
          final StringBuilder className = new StringBuilder();
          className
              .append('L')
              .append(type.getElementType().getClassName())
              .append(';');
          
          for (int i = 0; i < type.getDimensions(); i++) {
            className.insert(0, '[');
          }
          
          return asClass(className.toString());
        }
      case Type.OBJECT:
        return asClass(type.getClassName());
      default:
        return null;
    }
  }
  
  private static Class<?> asClass(String clazz) {
    try {
      return Class.forName(clazz);
    }
    catch (ClassNotFoundException e) {
      throw new FallDownException(e);
    }
  }
  
  public static Class<?>[] argumentsToClasses(String parameters) {
    final Type[] argumentTypes = Type.getType(parameters).getArgumentTypes();
    
    final Class<?>[] classes = new Class[argumentTypes.length];
    for (int i = 0; i < argumentTypes.length; i++) {
      classes[i] = typeToClass(argumentTypes[i]);
    }
    
    return classes;
  }
  
  public static Method getMethod(
      Class<?> clazz,
      String methodName,
      String descriptor) {
    try {
      return clazz.getMethod(methodName, argumentsToClasses(descriptor));
    }
    catch (Exception e) {
      throw new FallDownException(e);
    }
  }
  
  
  public static void main(String... args) throws Exception {
    final String parameters =
        "(IZF[I[[Ljava/lang/String;JDLjava/lang/String;)V";
    
    for (Class<?> c : argumentsToClasses(parameters))
      System.out.println(c);
    
    System.out.println("--------------");
    final Class<?> x = Class.forName("[Ljava.lang.String;");
    System.out.println(x);
    System.out.println("--------------");
  }
  
}
