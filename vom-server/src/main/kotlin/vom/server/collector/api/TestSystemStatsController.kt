package vom.server.collector.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import vom.server.collector.stats.SystemPerf
import vom.server.collector.stats.repository.SystemStatsRepository

@RestController
@RequestMapping("/test/system-stats/")
class TestSystemStatsController(
  val repository: SystemStatsRepository
) {

  @GetMapping
  fun list(@PathVariable perf: SystemPerf) =
    repository.findAllById(perf.id)


}
