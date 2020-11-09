package vom.server.collector.trove

import java.io.Serializable

data class ExecChaserId(
  val id: String,
  val collected: Long
) : Serializable {

  override fun equals(other: Any?): Boolean {
    if (other is ExecChaserId) {
      return this.id == other.id
        && this.collected == other.collected
    }

    return false
  }

  override fun hashCode(): Int {
    var result = id.hashCode()
    result = 31 * result + collected.hashCode()
    return result
  }

}
