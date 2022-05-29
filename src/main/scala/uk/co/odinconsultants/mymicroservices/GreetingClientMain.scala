package uk.co.odinconsultants.mymicroservices

import cats.effect.kernel.Async
import cats.effect.kernel.Sync
import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import cats.implicits._
import org.http4s.client.Client
import org.http4s.Request
import org.http4s.EntityDecoder

object GreetingClientMain extends IOApp.Simple:
  def run: IO[Unit] =
    makeCall[IO].as(ExitCode.Success)

  def printReturning[F[_], T](t: T)(implicit F: Sync[F]): F[T] = F.delay({
    println(t)
    t
  })

  def makeCall[F[_]: Async: Sync]: F[Unit] =
    val stream: Stream[F, Jokes.Joke] = for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      jokeAlg: Jokes[F] = GreetingClient.impl[F](Http4sHandler(client))
      joke   <- Stream.eval(jokeAlg.get.flatMap(printReturning))
    } yield {
      joke
    }
    stream.compile.drain