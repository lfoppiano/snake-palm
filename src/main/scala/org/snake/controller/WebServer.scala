package org.snake.controller

import java.util

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{Http, server}
import akka.stream.ActorMaterializer
import org.grobid.core.engines.{DateParser, MultiDateParser, NERParsers}
import org.grobid.core.main.{GrobidHomeFinder, LibraryLoader}
import org.grobid.core.utilities.GrobidProperties
import org.snake.engine.Parser

import scala.concurrent.Future
import scala.io.StdIn

object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()

    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher


    //-- GROBID INITIALISATION --
    val grobidHome = "/Users/lfoppiano/development/inria/grobid/grobid-home"
    val grobidHomeFinder = new GrobidHomeFinder(util.Arrays.asList(grobidHome))

    grobidHomeFinder.findGrobidHomeOrFail();
    GrobidProperties.getInstance(grobidHomeFinder)
    LibraryLoader.load()
    // End GROBID INITIALISATION
    val nerParsers = new NERParsers()
    val dateParser = new DateParser()
    val multiDateParser = new MultiDateParser()

    val parser = new Parser(nerParsers.getParser("en"), multiDateParser, dateParser)

    val route: server.Route =
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say welcome to the jungle.</h1>"))
      } ~
        post {
          path("process") {
            entity(as[String]) { text =>
              val result = Future.apply(parser.parse(text))

              onComplete(result) { done =>
                complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Data processed!</h1>"))
              }
            }
          }
        }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}