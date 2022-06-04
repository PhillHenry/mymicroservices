package uk.co.odinconsultants.mymicroservices

import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s.*
import org.http4s.implicits.*
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
    assertIO(handle(happyHandler), Joke)
  }

  test("Unhappy path call") {
    interceptIO[JokeError](handle(badHandler))
  }

  def handle(handler: HttpHandler[IO]): IO[Jokes.Joke] = GreetingClient.impl[IO](handler).get
}
