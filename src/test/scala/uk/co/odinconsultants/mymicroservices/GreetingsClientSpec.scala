package uk.co.odinconsultants.mymicroservices

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite

import uk.co.odinconsultants.mymicroservices.HttpHandler

class GreetingsClientSpec extends CatsEffectSuite {

  val Joke = Jokes.Joke("Boris Johnson")

  test("Happy path call") {
    val handler = new HttpHandler[IO]{
        def callHttp(req: Request[IO]): IO[Jokes.Joke] = IO.pure(Joke)
    }
    assertIO(GreetingClient.impl[IO](handler).get, Joke)
  }
}
