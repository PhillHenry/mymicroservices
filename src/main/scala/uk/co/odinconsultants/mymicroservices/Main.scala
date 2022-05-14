package uk.co.odinconsultants.mymicroservices

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp.Simple:
  def run: IO[Unit] =
    MymicroservicesServer.stream[IO].compile.drain.as(ExitCode.Success)

