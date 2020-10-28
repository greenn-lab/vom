package vom.client.bci.trove;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static vom.client.bci.utility.OpcodeUtils.VOID_LONG;
import static vom.client.bci.utility.OpcodeUtils.VOID_OBJECT;

@Getter
public class Trove {

  public static final ThreadLocal<Trove> BOOTY =
    new ThreadLocal<Trove>();

  private static final String INTERNAL_NAME =
    Type.getInternalName(Trove.class);
  private static final String CALL_SEIZE = "seize";
  private static final String CALL_EXPEL = "expel";
  private static final String CALL_CHASE = "chase";
  private static final String CALL_GLEAN = "glean";
  private static final String CALL_BRING = "bring";
  private static final String CALL_TAKEN = "taken";
  private static final String CALL_ERROR = "error";

  private static final String VOID_SEIZE =
    "(JLjava/lang/Object;Ljava/lang/Object;)V";
  private static final String VOID_CHASE =
    "(" + Type.getDescriptor(Chaser.class) + ")V";
  private static final String VOID_EXPEL = "(JLjava/lang/Object;)V";
  private static final String VOID_ERROR = "(JLjava/lang/Object;Ljava/lang/Throwable;)V";


  private final String id;
  private final Long collected;
  private final String method;
  private final String uri;
  private final Object starter;

  @Setter
  private Map<String, Serializable> headers = new HashMap<String, Serializable>();

  @Setter
  private Map<String, String[]> parameters = new HashMap<String, String[]>();

  @Setter
  private List<Chaser> dregs = new ArrayList<Chaser>();

  @Setter
  private long elapsed;

  @Setter
  private SQLChaser currentQuery;

  @Setter
  private Throwable error;


  @Builder
  public Trove(
    String id,
    Long collected,
    String method,
    String uri,
    Object starter
  ) {
    this.id = id;
    this.collected = collected;
    this.method = method;
    this.uri = uri;
    this.starter = starter;
  }


  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public void addBooty(Chaser booty) {
    dregs.add(booty);
  }


  public static void seize(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_SEIZE,
      VOID_SEIZE,
      false);
  }

  @SuppressWarnings({"unused", "unchecked"})
  public static void seize(long started, Object request, Object starter) {
    Trove trove = BOOTY.get();
    if (null != trove) {
      return;
    }

    try {
      final Class<?> clazz = request.getClass();

      final String method =
        (String) clazz.getMethod("getMethod").invoke(request);
      final String uri =
        (String) clazz.getMethod("getRequestURI").invoke(request);

      trove = Trove.builder()
        .id(Config.getId())
        .collected(started)
        .method(method)
        .uri(uri)
        .starter(starter)
        .build();

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

      BOOTY.set(trove);
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
  public static void expel(long elapsed, Object finisher) {
    expel(elapsed, finisher, null);
  }

  @SuppressWarnings("unused")
  public static void expel(long elapsed, Object finisher, Throwable error) {
    final Trove trove = BOOTY.get();
    if (null == trove
      || trove.getStarter() != finisher) return;

    trove.setElapsed(elapsed);
    trove.setError(error);

//    ServerConnection.give(trove);

    if (Config.isDebugMode()) {
      final StringBuilder logs = new StringBuilder();
      logs.append("Trove----------------------------\n");
      logs.append(String.format("[%s]%n", trove.uri));
      for (Chaser booty : trove.dregs) {
        logs.append(String.format(
          "%dms(%d ~ %d) %s%n",
          booty.getArrived() - booty.getStarted(),
          booty.getArrived(),
          booty.getStarted(),
          booty.signature())
        );
      }
      logs.append(String.format(
        "------------------------------%3d%n",
        trove.dregs.size())
      );

      System.out.println(logs);
    }

//    TROVE.remove();
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
    booty.keep(BOOTY.get());
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
    final Trove trove = BOOTY.get();
    if (trove == null) return;

    final SQLChaser currentQuery = trove.getCurrentQuery();
    if (currentQuery == null) return;

    currentQuery.addArgument(argument);
  }

  public static void bring(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      INTERNAL_NAME,
      CALL_BRING,
      VOID_CHASE,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void bring(Chaser chaser) {
    chaser.arrived();
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
    final Trove trove = BOOTY.get();
    if (trove == null) return;

    final SQLChaser currentQuery = trove.getCurrentQuery();
    if (currentQuery == null) return;

    currentQuery.setStarted(started);
    currentQuery.arrived();
  }

}
