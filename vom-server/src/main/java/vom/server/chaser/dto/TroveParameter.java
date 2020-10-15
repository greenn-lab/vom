package vom.server.chaser.dto;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

//@Entity
//@SequenceGenerator(
//  name = "TROVE_PARAMETER_TURN_SQ",
//  allocationSize = 1
//)
public class TroveParameter {

  @Id
  private String key;

  @Id
  @GeneratedValue(
    strategy = GenerationType.SEQUENCE,
    generator = "TROVE_PARAMETER_TURN_SQ"
  )
  private Long turn;

  private String type;

  private String value;

}
