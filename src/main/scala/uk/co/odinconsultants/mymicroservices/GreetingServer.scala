package uk.co.odinconsultants.mymicroservices

import cats.effect.{Async, Resource}
import fs2.Stream
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.Logger
import cats.implicits.*
import com.comcast.ip4s.*
import org.http4s.implicits.*

object GreetingServer {

  def stream[F[_] : Async](port: Port): Stream[F, Nothing] = {
    for {
      client        <- Stream.resource(EmberClientBuilder.default[F].build)
      helloWorldAlg = HelloWorld.impl[F]
      jokeAlg       = Jokes.impl[F](client)

      httpApp       = (
        MymicroservicesRoutes.helloWorldRoutes[F](helloWorldAlg)
//          <+> MymicroservicesRoutes.jokeRoutes[F](jokeAlg)
        ).orNotFound

      // With Middlewares in place
      finalHttpApp  = Logger.httpApp(true, true)(httpApp)

      exitCode      <- Stream.resource(
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port)
          .withHttpApp(finalHttpApp)
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain
}
