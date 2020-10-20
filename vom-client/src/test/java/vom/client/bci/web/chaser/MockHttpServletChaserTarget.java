package vom.client.bci.web.chaser;

public class MockHttpServletChaserTarget {

  public void methodA(int param1, String param2) {
    System.out.printf(
        "%s.methodA(param1: %d, param2: %s)%n",
        MockHttpServletChaserTarget.class.getName(),
        param1,
        param2
    );
  }

  public void methodB() {
    System.out.printf(
        "%s.methodB()%n",
        MockHttpServletChaserTarget.class.getName()
    );
  }

  public void methodC(boolean param1, char param2, byte param3, short param4, int param5, float param6, long param7, double param8) {
    System.out.printf(
        "%s.methodC(...)%n",
        MockHttpServletChaserTarget.class.getName()
    );
  }

  public String shout(int count) {
    final String format = "I shout %dth!!!!";
    return String.format(format, count);
  }

}
