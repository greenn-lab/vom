package vom.server.collector.trove

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "EXEC_CHASER")
class ExecChaser(
  @Id @GeneratedValue(GenerationType.IDENTITY) val id: Long
) : Serializable {

  val collected: Long = 0

  val uri: String = ""

  var method: String = ""

  @Lob
  val json: String = ""

}
