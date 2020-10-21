package vom.client.bci.trove;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import vom.client.Config;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static vom.client.bci.trove.Chasing.CHASE_INTERNAL;
import static vom.client.bci.utility.OpcodeUtils.VOID_LONG;
import static vom.client.bci.utility.OpcodeUtils.VOID_OBJECT;
import static vom.client.bci.utility.OpcodeUtils.VOID_THROWS;

@Getter
public class Trover implements Serializable {

  public static final ThreadLocal<Trover> TROVE = new ThreadLocal<Trover>();

  private static final String TROVER_INTERNAL =
    Type.getInternalName(Trover.class);
  private static final String TROVER_SEIZE = "seize";
  private static final String TROVER_EXPEL = "expel";
  private static final String TROVER_CHASE = "chase";
  private static final String TROVER_QUERY = "query";
  private static final String TROVER_GLEAN = "glean";
  private static final String TROVER_BRING = "bring";
  private static final String TROVER_TAKEN = "taken";
  private static final String TROVER_VOMIT = "vomit";

  private static final String VOID_SEIZE =
    "(Ljavax/servlet/http/HttpServletRequest;J)V";
  private static final String VOID_CHASE =
    "(L" + CHASE_INTERNAL + ";)V";
  private static final String VOID_BRING =
    "(L" + CHASE_INTERNAL + ";)V";


  private final String id;
  private final Long collected;
  private final String method;
  private final String uri;

  @Setter
  private Map<String, String> headers = new HashMap<String, String>();

  @Setter
  private Map<String, String[]> parameters = new HashMap<String, String[]>();

  @Setter
  @Getter
  private List<Chasing> booties = new ArrayList<Chasing>();

  @Setter
  private long elapsed;

  @Getter
  @Setter
  private QueryInChasing currentQuery;

  private VomitInChasing vomit;


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

  public void setVomit(VomitInChasing vomit) {
    if (this.vomit == null) {
      this.vomit = vomit;
    }
  }


  public static void seize(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_SEIZE,
      VOID_SEIZE,
      false);
  }

  @SuppressWarnings("unused")
  public static void seize(HttpServletRequest request, long started) {
    if (request == null) return;

    final Trover trove = Trover.builder()
      .id(Config.getId())
      .collected(started)
      .method(request.getMethod())
      .uri(request.getRequestURI())
      .build();

    @SuppressWarnings("unchecked") final Map<String, String[]> parameterMap
      = request.getParameterMap();
    trove.setParameters(parameterMap);

    @SuppressWarnings("unchecked") final Enumeration<String> headerNames =
      request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      final String name = headerNames.nextElement();
      trove.addHeader(name, request.getHeader(name));
    }

    TROVE.set(trove);
  }

  public static void expel(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_EXPEL,
      VOID_LONG,
      false);
  }

  @SuppressWarnings("unused")
  public static void expel(long elapsed) {
    final Trover trove = TROVE.get();
    if (null == trove) return;

    trove.setElapsed(elapsed);

    try {
      final ByteArrayOutputStream outByteArray
        = new ByteArrayOutputStream();
      final ObjectOutputStream out = new ObjectOutputStream(outByteArray);

      out.writeObject(trove);
      out.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    TROVE.remove();
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
    final Trover trove = TROVE.get();
    if (trove == null) return;

    trove.addBooty(booty);
  }

  public static void query(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_QUERY,
      VOID_CHASE,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void query(Chasing booty) {
    final Trover trove = TROVE.get();
    if (trove == null) return;

    trove.setCurrentQuery((QueryInChasing) booty);
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
      VOID_BRING,
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

  public static void vomit(MethodVisitor mv) {
    mv.visitMethodInsn(
      INVOKESTATIC,
      TROVER_INTERNAL,
      TROVER_VOMIT,
      VOID_THROWS,
      false
    );
  }

  @SuppressWarnings("unused")
  public static void vomit(Throwable cause) {
    final Trover trove = TROVE.get();
    if (trove == null) return;

    trove.setVomit(new VomitInChasing(cause));
  }

}
