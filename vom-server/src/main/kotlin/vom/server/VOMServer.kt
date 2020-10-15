package vom.server

import org.h2.tools.Server
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VOMServer : CommandLineRunner {

  override fun run(vararg args: String?) {
    val server = Server
      .createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "3506")
      .start()

    Runtime.getRuntime().addShutdownHook(
      Thread(Runnable {
        if (server.isRunning(false)) {
          server.stop()
        }
      })
    )
  }

}

fun main(args: Array<String>) {
  runApplication<VOMServer>(*args)
}
