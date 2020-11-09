package vom.server.collector.trove

import java.io.Serializable
import javax.persistence.*

@Entity
@Table(name = "EXEC_CHASER")
@IdClass(ExecChaserId::class)
class ExecChaser(
  @Id val id: String,
  @Id val collected: Long
) : Serializable {

  @Lob
  val json: String = ""

}
