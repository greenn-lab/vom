package vom.server.collector.stats.repository

import org.springframework.data.jpa.repository.JpaRepository
import vom.server.collector.stats.SystemPerf
import vom.server.collector.stats.SystemPerfId

interface SystemStatsRepository : JpaRepository<SystemPerf, SystemPerfId> {

  fun findAllById(id: String)

}
