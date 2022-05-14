package uk.co.odinconsultants.mymicroservices

import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import uk.co.odinconsultants.mymicroservices.Jokes.{Joke, JokeError}
import cats.effect.Concurrent
import org.http4s._
import org.http4s.implicits._
import cats.implicits.catsSyntaxMonadError

object GreetingClient {
  def impl[F[_]: Concurrent](C: Client[F]): Jokes[F] = new Jokes[F]:
    val dsl = new Http4sClientDsl[F]{}
    import dsl._
    def get: F[Jokes.Joke] =
      C.expect[Joke](GET(uri"https://icanhazdadjoke.com/"))
        .adaptError{ case t => JokeError(t)}
}
