package vom.server.collector.trover

import javax.persistence.*
import javax.persistence.GenerationType.SEQUENCE

@Entity
@SequenceGenerator(
  name = "SQ_VAULT_ID",
  sequenceName = "SQ_VAULT_ID",
  allocationSize = 1
)
class Vault {

  @Id
  @GeneratedValue(strategy = SEQUENCE, generator = "SQ_VAULT_ID")
  var id: Long = 0

  var agentId: String = ""

  var collected: Long = 0

  var method: String = ""

  var url: String = ""

  var elapsed: Int = 0

  @Lob
  var json: String = ""

}
