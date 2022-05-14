package uk.co.odinconsultants.mymicroservices

import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import cats.implicits._

object GreetingClientMain extends IOApp.Simple:
  def run: IO[Unit] =
    makeCall.as(ExitCode.Success)

//  def makeCall[IO[_]: Async]: IO[Unit] =
  def makeCall: IO[Unit] =
    val stream: Stream[IO, Jokes.Joke] = for {
      client <- Stream.resource(EmberClientBuilder.default[IO].build)
      jokeAlg: Jokes[IO] = GreetingClient.impl[IO](client)
      joke   <- Stream.eval(jokeAlg.get.flatMap(printAndReturn))
    } yield {
      joke
    }
    stream.compile.drain

  def printAndReturn[T](t: T): IO[T] =
    IO.println(s"printAndReturn: $t") >> IO(t)