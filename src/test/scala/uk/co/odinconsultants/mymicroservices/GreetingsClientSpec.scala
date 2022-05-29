package uk.co.odinconsultants.mymicroservices

import cats.effect.IO
import org.http4s._
import org.http4s.implicits._
import munit.CatsEffectSuite

import uk.co.odinconsultants.mymicroservices.HttpHandler
import uk.co.odinconsultants.mymicroservices.Jokes.JokeError

class GreetingsClientSpec extends CatsEffectSuite {

  val Joke = Jokes.Joke("Boris Johnson")
  val happyHandler = new HttpHandler[IO] {
    def callHttp(req: Request[IO]): IO[Jokes.Joke] = IO.pure(Joke)
  }

  val Failure = new Throwable("error message")
  val badHandler = new HttpHandler[IO] {
      def callHttp(req: Request[IO]): IO[Jokes.Joke] = IO.raiseError(Failure)
  }

  test("Happy path call") {
    assertIO(GreetingClient.impl[IO](happyHandler).get, Joke)
  }

  test("Unhappy path call") {
    val io = GreetingClient.impl[IO](badHandler).get
    interceptIO[JokeError](io)
  }
}
