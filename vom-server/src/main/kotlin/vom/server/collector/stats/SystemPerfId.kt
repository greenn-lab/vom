package vom.server.collector.stats

import java.io.Serializable

data class SystemPerfId(
  var id: String = "",
  var collected: Long = 0
) : Serializable
