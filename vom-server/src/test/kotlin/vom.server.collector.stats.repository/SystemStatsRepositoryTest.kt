package vom.server.collector.stats.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import vom.server.collector.stats.SystemStats

@SpringBootTest
class SystemStatsRepositoryTest @Autowired constructor(
  val repo: SystemStatsRepository
) {

  @Test
  fun `Should insert system statistics successfully`() {
    val stats: SystemStats = SystemStats("test", 123)
      .apply {
        cpu = 123.1
        diskFree = 3
        diskTotal = 4
        memoryFree = 5
        memoryTotal = 6
        networkReceived = 7
        networkSent = 8
        networkBandwidth = 9
      }

    val save = repo.save(stats)

    Assertions.assertNotNull(save)
  }

}
