package vom.server.collector.trove.repository

import org.springframework.data.jpa.repository.JpaRepository
import vom.server.collector.trove.ExecChaser
import vom.server.collector.trove.ExecChaserId

interface ExecChaserRepository : JpaRepository<ExecChaser, ExecChaserId>
