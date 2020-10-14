package vom.server.chaser.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SequenceGenerator(
  name = "TROVE_PARAMETER_TURN_SQ",
  allocationSize = 1
)
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
