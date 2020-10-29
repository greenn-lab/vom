package vom.client.bci.trove;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static vom.client.bci.trove.Chaser.CHASER_INTERNAL;
import static vom.client.bci.utility.OpcodeUtils.VOID_LONG;
import static vom.client.bci.utility.OpcodeUtils.VOID_NONE;
import static vom.client.bci.utility.OpcodeUtils.VOID_OBJECT;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TroveExecutor {

  public static final ThreadLocal<Trove> TROVE =
    new ThreadLocal<Trove>();

  private static final String INTERNAL_NAME =
    Type.getInternalName(TroveExecutor.class);
  private static final String CALL_SEIZE = "seize";
  private static final String CALL_EXPEL = "expel";
  private static final String CALL_CHASE = "chase";
  private static final String CALL_GLEAN = "glean";
  private static final String CALL_CLOSE = "close";
  private static final String CALL_TAKEN = "taken";
  private static final String CALL_ERROR = "error";

  private static final String VOID_SEIZE =
    "(Ljava/lang/Object;Ljava/lang/Object;)V";
  private static final String VOID_CHASE =
    "(" + Type.getDescriptor(Chaser.class) + ")V";
  private static final String VOID_EXPEL = "(JLjava/lang/Object;)V";
  private static final String VOID_ERROR = "(JLjava/lang/Object;Ljava/lang/Throwable;)V";


  public static void seize(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_SEIZE,
      VOID_SEIZE,
      false);
  }

  @SuppressWarnings({"unused", "unchecked"})
  public static void seize(Object request, Object starter) {
    Trove trove = TROVE.get();
    if (null != trove) {
      return;
    }

    try {
      final Class<?> clazz = request.getClass();

      final String method =
        (String) clazz.getMethod("getMethod").invoke(request);
      final String uri =
        (String) clazz.getMethod("getRequestURI").invoke(request);

      trove = new Trove(
        method,
        uri,
        starter
      );

      trove.setParameters(
        (Map<String, String[]>) clazz.getMethod("getParameterMap")
          .invoke(request)
      );

      final Enumeration<String> headerNames =
        (Enumeration<String>) clazz.getMethod("getHeaderNames").invoke(request);
      while (headerNames.hasMoreElements()) {
        final String name = headerNames.nextElement();

        trove.addHeader(
          name,
          (String) clazz.getMethod("getHeader", String.class)
            .invoke(request, name)
        );
      }

      TROVE.set(trove);
    }
    catch (Throwable cause) {
      cause.printStackTrace();
    }
  }


  public static void expel(MethodVisitor mv) {
    expel(mv, false);
  }

  public static void expel(MethodVisitor mv, boolean hasError) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_EXPEL,
      hasError ? VOID_ERROR:VOID_EXPEL,
      false);
  }

  @SuppressWarnings("unused")
  public static void expel(long finished, Object finisher) {
    expel(finished, finisher, null);
  }

  @SuppressWarnings("unused")
  public static void expel(long finished, Object finisher, Throwable error) {
    final Trove trove = TROVE.get();
    if (null == trove
      || trove.getStarter() != finisher) return;

    trove.setFinished(finished);
    trove.setError(error);

    if (Config.isDebugMode()) {
      print(trove);
    }

/*
    ServerConnection.give(trove);
    TROVE.remove();
*/
  }


  public static void chase(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_CHASE,
      VOID_CHASE,
      false);
  }

  @SuppressWarnings("unused")
  public static void chase(Chaser booty) {
    booty.keeping(TROVE.get());
  }

  public static void glean(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_GLEAN,
      VOID_OBJECT,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void glean(Object argument) {
    final Trove trove = TROVE.get();
    if (trove == null) return;

    final SQLChaser currentQuery = trove.getCurrentQuery();
    if (currentQuery == null) return;

    currentQuery.addArgument(argument);
  }


  public static void close(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKEVIRTUAL,
      CHASER_INTERNAL,
      CALL_CLOSE,
      VOID_NONE,
      false
    );
  }


  public static void taken(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_TAKEN,
      VOID_LONG,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void taken(long started) {
    final Trove trove = TROVE.get();
    if (trove == null) return;

    final SQLChaser currentQuery = trove.getCurrentQuery();
    if (currentQuery == null) return;

    currentQuery.setStarted(started);
    currentQuery.close();
  }


  private static void print(Trove trove) {
    final StringBuilder logs = new StringBuilder();
    logs.append(String.format("[%s]---%n", trove.getUri()));

    for (Map.Entry<String, String[]> param : trove.getParameters().entrySet()) {
      logs.append(
        String.format("\t- %s: %s",
          param.getKey(),
          Arrays.toString(param.getValue()))
      );
    }

    for (Chaser dreg : trove.getDregs()) {
      logs.append(String.format(
        "%6dms (%s) %s%s%n",
        dreg.getArrived() - dreg.getStarted(),
        msPeriod(dreg.getStarted(), dreg.getArrived()),
        dreg.signature(),
        dreg instanceof JSPChaser ? "" : printArguments(dreg.getArguments()))
      );
    }
    logs.append(String.format(
      "%6dms (%s) elapsed ---%n",
      trove.elapse(),
      msPeriod(trove.getCollected(), trove.getFinished()))
    );

    System.out.println(logs);
  }

  private static String printArguments(List<Trove.Argument> args) {
    if (args.isEmpty()) return "()";

    final StringBuilder result = new StringBuilder();
    result.append('(');

    for (Trove.Argument arg : args) {
      final String name = arg.getType().getCanonicalName();

      result
        .append(name.substring(name.lastIndexOf(".") + 1))
        .append(",");
    }

    result.setCharAt(result.length() - 1, ')');

    return result.toString();
  }

  private static String msPeriod(long start, long end) {
    final String startString = String.valueOf(start);
    final String endString = String.valueOf(end);

    final char[] startChars = startString.toCharArray();
    final char[] endChars = String.valueOf(end).toCharArray();

    int i;
    for (i = 0; i < startChars.length - 1;) {
      if (startChars[i] != endChars[i]) {
        break;
      }

      i++;
    }

    return String.format(
      "%s ~ %s", startString.substring(i), endString.substring(i));
  }

}
