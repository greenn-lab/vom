package vom.server.chaser.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

//@Entity
public class SystemStats {

  @Id
  private Long id;

  @Id
  private Long collected;

  private Double cpu;

  private Long diskFree;
  private Long diskTotal;

  private Long memoryFree;
  private Long memoryTotal;

  private Long networkTotal;
  private Long networkReceived;
  private Long networkSent;

//  private Double temperature;
//  private Double voltage;

}
