package vom.server.chaser.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Trove {

  @Id
  private String key;

  @Enumerated(EnumType.STRING)
  private Type type;

  @Lob
  private byte[] data;

  @OneToMany
  private List<TroveParameter> parameters = new ArrayList<>();


  enum Type {
    WEB,
    DB
  }

}
