package vom.server.collector.trove.repository

import org.springframework.data.jpa.repository.JpaRepository
import vom.server.collector.trove.ExecChaser

interface ExecChaserRepository : JpaRepository<ExecChaser, Long>
