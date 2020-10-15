package vom.server.collector.stats

import javax.persistence.*

@Entity
@Table(name = "SYSTEM_STATS")
@IdClass(SystemStatsId::class)
class SystemStats(
  @Id var id: String,
  @Id var collected: Long
) {

  var cpu: Double = -1.0

  @Column(name = "DISK_FREE")
  var diskFree: Long = -1

  @Column(name = "DISK_TOTAL")
  var diskTotal: Long = -1

  @Column(name = "MEM_FREE")
  var memoryFree: Long = -1

  @Column(name = "MEM_TOTAL")
  var memoryTotal: Long = -1

  @Column(name = "NET_RECV")
  var networkReceived: Long = -1

  @Column(name = "NET_SENT")
  var networkSent: Long = -1

  @Column(name = "NET_BW")
  var networkBandwidth: Long = -1

}
