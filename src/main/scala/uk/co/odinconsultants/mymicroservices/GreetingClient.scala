package uk.co.odinconsultants.mymicroservices

import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import uk.co.odinconsultants.mymicroservices.Jokes.{Joke, JokeError}
import cats.effect.Concurrent
import org.http4s._
import org.http4s.implicits._
import cats.implicits.catsSyntaxMonadError

trait HttpHandler[F[_]] {
  def callHttp(req: Request[F]): F[Jokes.Joke]
}

class Http4sHandler[F[_]: Concurrent](C: Client[F], d: EntityDecoder[F, Jokes.Joke]) extends HttpHandler[F] {
  def callHttp(req: Request[F]): F[Jokes.Joke] = {
    C.expect[Joke](req)(d) 
  }
}

object Http4sHandler{
  def apply[F[_]: Concurrent](C: Client[F])(implicit d: EntityDecoder[F, Jokes.Joke]): Http4sHandler[F] = new Http4sHandler(C, d)
}

object GreetingClient {
  def impl[F[_]: Concurrent](handler: HttpHandler[F]): Jokes[F] = new Jokes[F]:
    val dsl = new Http4sClientDsl[F]{}
    import dsl._
    def get: F[Jokes.Joke] =
      handler.callHttp(GET(uri"https://icanhazdadjoke.com/"))
        .adaptError{ case t => JokeError(t)}
}
