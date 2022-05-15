package uk.co.odinconsultants.mymicroservices

import cats.effect.kernel.Async
import cats.effect.kernel.Sync
import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.implicits._
import cats.implicits._

object GreetingClientMain extends IOApp.Simple:
  def run: IO[Unit] =
    makeCall[IO].as(ExitCode.Success)
    
  def printing[T](t: T): _ => T = _ => t

  def printAnReturn[F[_], T](t: T)(implicit F: Async[F]): F[T] = F.delay({
    println(t)
    t
  })

  def makeCall[F[_]: Async: Sync]: F[Unit] =
    val stream: Stream[F, Jokes.Joke] = for {
      client <- Stream.resource(EmberClientBuilder.default[F].build)
      jokeAlg: Jokes[F] = GreetingClient.impl[F](client)
      joke   <- Stream.eval(jokeAlg.get.flatMap(printAnReturn))
    } yield {
      joke
    }
    stream.compile.drain