package vom.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

class BootstrapTest {
  
  @Test
  @DisplayName("Agent Booting!")
  void shouldRunVOMClient() throws InterruptedException {
    do {
      TimeUnit.SECONDS.sleep(1);
    } while (!new Scanner(System.in).nextLine().equals("exit"));
  }
  
}
