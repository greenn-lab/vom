package vom.server.collector.stats.repository

import org.springframework.data.jpa.repository.JpaRepository
import vom.server.collector.stats.SystemStats
import vom.server.collector.stats.SystemStatsId

interface SystemStatsRepository
  : JpaRepository<SystemStats, SystemStatsId> {

  fun findAllById(id: String)

}
