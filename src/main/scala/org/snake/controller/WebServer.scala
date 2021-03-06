package org.snake.controller

import java.util

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.{Http, server}
import akka.stream.ActorMaterializer
import akka.util.ByteString
import org.grobid.core.data.dates.Period
import org.grobid.core.engines.{DateParser, NERParsers, TemporalExpressionParser}
import org.grobid.core.main.{GrobidHomeFinder, LibraryLoader}
import org.grobid.core.utilities.GrobidProperties
import org.snake.engine.Parser

import scala.collection.mutable
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

    grobidHomeFinder.findGrobidHomeOrFail()
    GrobidProperties.getInstance(grobidHomeFinder)
    LibraryLoader.load()
    // End GROBID INITIALISATION

    val nerParsers = new NERParsers()
    val dateParser = new DateParser()
    val temporalExpressionParser = new TemporalExpressionParser()

    val parser = new Parser(nerParsers.getParser("en"), temporalExpressionParser, dateParser)

    val route: server.Route =

      pathPrefix("demo") {
        get {
          encodeResponse {
            getFromResourceDirectory("demo")
          }
        }
      } ~
        pathEnd {
          get {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say welcome to the jungle.</h1>"))
          }
        } ~
        path("process") {
          post {
            entity(as[String]) { text =>
              val result = parser.parse(text)
              val analytics: mutable.Map[String, String] = result("analytics").asInstanceOf[mutable.Map[String, String]]
              val dates: List[Period] = result("dates").asInstanceOf[List[Period]]

              val jsons = dates.map(f => f.toJson())


              val start = """{"""
              val end = """}"""
              val dateStart = """"dates":["""
              val dateEnd ="""]"""

              val datesJson = jsons.mkString(dateStart, ",", dateEnd)

              val analyticsJson = s""""analytics": { "minDate": "${analytics("minDate")}", "maxDate": "${analytics("maxDate")}"}"""

              val json = s"${start} ${datesJson}, ${analyticsJson} ${end}"

              complete(HttpEntity(ContentTypes.`application/json`, ByteString(json)));
            }
          }
        }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

    println(s"Server online at http://localhost:8081/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}