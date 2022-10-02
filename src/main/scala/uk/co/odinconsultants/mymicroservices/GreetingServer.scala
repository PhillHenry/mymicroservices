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
      exitCode      <- Stream.resource(
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port)
          .withHttpApp(Logger.httpApp(true, true)(
            MymicroservicesRoutes.helloWorldRoutes[F](HelloWorld.impl[F]).orNotFound)
          )
          .build >>
          Resource.eval(Async[F].never)
      )
    } yield exitCode
  }.drain
}
