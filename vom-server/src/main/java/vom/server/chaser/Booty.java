package vom.server.chaser;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@EqualsAndHashCode(of = {"id", "collected"}, callSuper = false)
@Getter
@Setter
public class Booty extends vom.client.chase.Booty implements Serializable {
  
  @Id
  private Long id;
  
  @Id
  private Long collected;

}
