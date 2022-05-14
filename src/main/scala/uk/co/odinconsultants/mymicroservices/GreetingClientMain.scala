package uk.co.odinconsultants.mymicroservices

import cats.effect.kernel.Async
import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import cats.implicits._

object GreetingClientMain extends IOApp.Simple:
  def run: IO[Unit] =
    makeCall[IO].as(ExitCode.Success)

  def makeCall[F[_]: Async]: F[Unit] =
    val stream: Stream[F, Jokes.Joke] = for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      jokeAlg: Jokes[F] = GreetingClient.impl[F](client)
      joke   <- Stream.eval(jokeAlg.get.flatMap(x => {println(x); x}.pure)) // hmm, that's not pure...
    } yield {
      joke
    }
    stream.compile.drain