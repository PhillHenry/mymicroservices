package uk.co.odinconsultants.mymicroservices

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.*
import cats.implicits._

object GreetingServerMain extends IOApp{

  def startServer(port: String): IO[ExitCode] =
    Port.fromString(port).map { port =>
      val result: IO[ExitCode] = GreetingServer.stream[IO](port).compile.drain.map(_ => ExitCode.Success)
      result
    }.getOrElse(IO {println(s"Could not start server on port $port")}.as(ExitCode.Error))

  def run(args: List[String]): IO[ExitCode] = {
    args.headOption.map { port =>
      startServer(port)
    }.getOrElse(IO {
      println(s"Could not start server with arguments: ${args.mkString(", ")}")
      ExitCode.Error
    })
  }

}
