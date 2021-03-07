package com.example

import java.io.InputStream
import java.security.{ KeyStore, SecureRandom }
import javax.net.ssl.{ KeyManagerFactory, SSLContext, TrustManagerFactory }

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{ ConnectionContext, Http, HttpsConnectionContext }

import scala.io.StdIn
import io.AnsiColor._

object SimpleHttpServer extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  val config = system.settings.config

  val useTLS = config.getBoolean("sample-app.enable-tls")
  val requireMutualAuth = config.getBoolean("sample-app.require-mutual-authentication")

  val host = "localhost"
  val port = if(useTLS) 8443 else 8080

  val password: Array[Char] = "bTNbuHwy7c".toCharArray // do not store passwords in code, read them from somewhere safe!
  val ks: KeyStore = KeyStore.getInstance("JKS")
  val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("example.com.jks")
  require(keystore != null, "Keystore required!")
  ks.load(keystore, password)
  val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
  keyManagerFactory.init(ks, password)
  val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
  tmf.init(ks)

  val sslContext: SSLContext = SSLContext.getInstance("TLS")
  sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
  val https: HttpsConnectionContext = ConnectionContext.httpsServer(() => {
      val engine = sslContext.createSSLEngine()
      engine.setUseClientMode(false)
      engine.setNeedClientAuth(requireMutualAuth)
      engine
    })

  val routes =
    (get & path("status")) { 
      complete(StatusCodes.OK -> s"${BOLD}${CYAN}Hello from Akka HTTP!!${RESET}") 
    }
  
  val bindingFuture = 
    if(useTLS) Http().newServerAt(host, port).enableHttps(https).bind(routes)
    else Http().newServerAt(host, port).bind(routes)

  bindingFuture.foreach { _ =>
    println(s"Server online at ${BOLD}http${if(useTLS) "s" else ""}://$host:$port${RESET}")
    println( (useTLS, requireMutualAuth) match {
      case (true, true) => s"${BOLD}${GREEN}Using mutual TLS authentication, in addition to server authentication performed by the client, the server requires client to send a certificate for authentication${RESET}"
      case (true, false) => s"${BOLD}${GREEN}Using server TLS authentication only, client requires server to send a certificate for authentication${RESET}"
      case _ => ""
    })
    println("Press RETURN to stop...")
  }

  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}