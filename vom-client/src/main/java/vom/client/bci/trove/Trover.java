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
public class Trover {

  public static final ThreadLocal<Trover> TROVE =
    new ThreadLocal<Trover>();

  private static final String TROVER_INTERNAL =
    Type.getInternalName(Trover.class);
  private static final String TROVER_SEIZE = "seize";
  private static final String TROVER_PAPER = "paper";
  private static final String TROVER_EXPEL = "expel";
  private static final String TROVER_CHASE = "chase";
  private static final String TROVER_QUERY = "query";
  private static final String TROVER_GLEAN = "glean";
  private static final String TROVER_BRING = "bring";
  private static final String TROVER_TAKEN = "taken";
  private static final String TROVER_VOMIT = "vomit";

  private static final String VOID_SEIZE =
    "(Ljava/lang/Object;J)V";
  private static final String VOID_CHASE =
    "(" + Type.getDescriptor(Chasing.class) + ")V";
  private static final String VOID_VOMIT1 = "(J)V";
  private static final String VOID_VOMIT2 = "(JLjava/lang/Throwable;)V";


  private final String id;
  private final Long collected;
  private final String method;
  private final String uri;

  @Setter
  private Map<String, Serializable> headers = new HashMap<String, Serializable>();

  @Setter
  private Map<String, String[]> parameters = new HashMap<String, String[]>();

  @Setter
  private List<Chasing> booties = new ArrayList<Chasing>();

  @Setter
  private long elapsed;

  @Setter
  private QueryInChasing currentQuery;

  @Setter
  private Throwable vomit;


  @Builder
  public Trover(String id, Long collected, String method, String uri) {
    this.id = id;
    this.collected = collected;
    this.method = method;
    this.uri = uri;
  }


  public void addHeader(String name, String value) {
    headers.put(name, value);
  }

  public void addBooty(Chasing booty) {
    booties.add(booty);
  }


  public static void seize(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_SEIZE,
      VOID_SEIZE,
      false);
  }

  @SuppressWarnings({"unused", "unchecked"})
  public static void seize(Object request, long started) {
    Trover trove = TROVE.get();
    if (null != trove) return;

    try {
      final Class<?> clazz = request.getClass();

      final String method =
        (String) clazz.getMethod("getMethod").invoke(request);
      final String uri =
        (String) clazz.getMethod("getRequestURI").invoke(request);

      trove = Trover.builder()
        .id(Config.getId())
        .collected(started)
        .method(method)
        .uri(uri)
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

      TROVE.set(trove);
    }
    catch (Throwable cause) {
      cause.printStackTrace();
    }
  }

  public static void expel(MethodVisitor mv) {
    expel(mv, false);
  }

  public static void expel(MethodVisitor mv, boolean withVomit) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_EXPEL,
      withVomit ? VOID_VOMIT2:VOID_VOMIT1,
      false);
  }

  @SuppressWarnings("unused")
  public static void expel(long elapsed) {
    expel(elapsed, null);
  }

  @SuppressWarnings("unused")
  public static void expel(long elapsed, Throwable vomit) {
    final Trover trove = TROVE.get();
    if (null == trove) return;

    trove.setElapsed(elapsed);
    trove.setVomit(vomit);

//    ServerConnection.give(trove);

    if (Config.isDebugMode()) {
      final StringBuilder logs = new StringBuilder();
      logs.append("Trove----------------------------\n");
      logs.append(String.format("[%s]%n", trove.uri));
      for (Chasing booty : trove.booties) {
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
        trove.booties.size())
      );

      System.out.println(logs);
    }

//    TROVE.remove();
  }

  public static void chase(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_CHASE,
      VOID_CHASE,
      false);
  }

  @SuppressWarnings("unused")
  public static void chase(Chasing booty) {
    booty.keep(TROVE.get());
  }

  public static void glean(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_GLEAN,
      VOID_OBJECT,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void glean(Object argument) {
    final Trover trove = TROVE.get();
    if (trove == null) return;

    final QueryInChasing currentQuery = trove.getCurrentQuery();
    if (currentQuery == null) return;

    currentQuery.addArgument(argument);
  }

  public static void bring(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_BRING,
      VOID_CHASE,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void bring(Chasing chasing) {
    chasing.arrived();
  }

  public static void taken(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_TAKEN,
      VOID_LONG,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void taken(long started) {
    final Trover trove = TROVE.get();
    if (trove == null) return;

    final QueryInChasing currentQuery = trove.getCurrentQuery();
    if (currentQuery == null) return;

    currentQuery.setStarted(started);
    currentQuery.arrived();
  }

}
