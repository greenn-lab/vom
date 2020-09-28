package vom.client;

public class ForShowByteCode {
  
  private final String say = "Ho~";
  
  public void methodA(int paramA, double paramB, long paramC) {
    Object[] someArray = new Object[3];
    
    someArray[0] = 123;
    someArray[1] = "456";
    someArray[2] = 789.0D;
  
    methodB(say, someArray);
    
    System.out.println("someArray: " + someArray);
    
  }
  
  public void methodB(String methodBparam1, Object[] methodBparam2) {
    
    String tmp = "temp";
  
    System.out.println(tmp + methodBparam1);
  
    System.out.println(methodBparam2);
  }
  
}
