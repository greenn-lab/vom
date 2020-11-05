package vom.client.bci.trove;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;
import vom.client.connector.ServerConnection;

import java.io.PrintWriter;
import java.io.StringWriter;
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

  private static final String DESC_SEIZE =
    "(Ljava/lang/Object;Ljava/lang/Object;)V";
  private static final String DESC_CHASE =
    "(" + Type.getDescriptor(Chaser.class) + ")V";
  private static final String DESC_EXPEL =
    "(JLjava/lang/Object;Ljava/lang/Object;)V";


  private TroveExecutor() {
    throw new IllegalStateException();
  }


  public static void seize(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_SEIZE,
      DESC_SEIZE,
      false);
  }

  @SuppressWarnings({"unused", "unchecked"})
  public static void seize(Object request, Object starter) {
    Trove trove = TROVE.get();

    if (null != trove) return;

    if (Config.getList("classes.ignore-servlet")
      .contains(starter.getClass().getName())) return;

    try {
      final Class<?> clazz = request.getClass();

      final String method =
        (String) clazz.getMethod("getMethod").invoke(request);
      final String uri =
        (String) clazz.getMethod("getRequestURI").invoke(request);

      trove = new Trove(method, uri, starter, request);
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
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_EXPEL,
      DESC_EXPEL,
      false);
  }


  @SuppressWarnings("unused")
  public static void expel(long finished, Object identifier, Object finisher) {
    final Trove trove = TROVE.get();
    final boolean error = identifier instanceof Throwable;

    if (null == trove) return;

    if (!error && (
      trove.getStarter() != finisher
        || trove.getIdentifier() != identifier
    )) return;

    trove.setFinished(finished);

    if (error) {
      trove.setError((Throwable) identifier);
    }

    ServerConnection.give(trove);

    TROVE.remove();
  }


  public static void chase(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_CHASE,
      DESC_CHASE,
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


  public static void print(Trove trove) {
    final StringBuilder logs = new StringBuilder();
    logs.append(String.format("%d [%s] ---%n",
      trove.getIdentifier().hashCode(),
      trove.getUri()));

    if (null != trove.getError()) {
      logs.append(String.format("\t|%s%n", trove.getError().getMessage()));
      final StringWriter out = new StringWriter();
      trove.getError().printStackTrace(new PrintWriter(out));
      logs.append(out.toString().replace("\n", "\n\t|"));
    }

    for (Map.Entry<String, String[]> param : trove.getParameters().entrySet()) {
      logs.append(
        String.format("\t- %s: %s%n",
          param.getKey(),
          Arrays.toString(param.getValue()))
      );
    }

    for (Chaser dreg : trove.getDregs()) {
      logs.append(String.format(
        "%6dms (%d ~ %d) %s%s%n",
        dreg.getArrived() - dreg.getStarted(),
        dreg.getStarted(), dreg.getArrived(),
        dreg.signature(),
        dreg instanceof JSPChaser ? "":printArguments(dreg.getArguments()))
      );
    }
    logs.append(String.format(
      "%6dms (%d ~ %d) elapsed ---%n",
      trove.elapse(),
      trove.getCollected(), trove.getFinished())
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

}
