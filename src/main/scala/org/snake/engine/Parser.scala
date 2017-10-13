package org.snake.engine

import java.io.InputStream
import java.util

import org.grobid.core.data.dates.Period
import org.grobid.core.engines._
import org.grobid.core.lexicon.NERLexicon.NER_Type
import org.grobid.core.main.{GrobidHomeFinder, LibraryLoader}
import org.grobid.core.utilities.GrobidProperties

import scala.collection.JavaConverters
import scala.io.Source

class Parser(parser: NERParser, intervalParser: TemporalExpressionParser, dParser: DateParser) {

  var nerParser: NERParser = parser
  var dateParser: DateParser = dParser
  var temporalExpressionParser: TemporalExpressionParser = intervalParser

  def parse(is: InputStream): List[Period] = {

    val text: String = scala.io.Source.fromInputStream(is).mkString
    parse(text)
  }

  def parse(text: String): List[Period] = {
    val entities = nerParser.extractNE(text)
    println(text)

    val entitiesScala = JavaConverters.asScalaBufferConverter(entities).asScala.toList

    println("All Named Entities")
    entitiesScala.foreach(x => println(x))

    val nerPeriods = {
      entitiesScala.filter(e => e.getType == NER_Type.PERIOD || e.getType == NER_Type.EVENT)
    }

    println("Only periods and dates")
    return nerPeriods.flatMap(nerEntityPeriod => {
      println("NER -> name: " + nerEntityPeriod.getRawName + ", type:" + nerEntityPeriod.getType)
      println("NER -> substring: " + text.substring(nerEntityPeriod.getOffsetStart, nerEntityPeriod.getOffsetEnd))

      val nerStart = nerEntityPeriod.getOffsetStart
      val nerEnd = nerEntityPeriod.getOffsetEnd

      val periods = temporalExpressionParser.process(nerEntityPeriod.getRawName)
      val periodsScala = JavaConverters.asScalaBufferConverter(periods).asScala.toList

      periodsScala.foreach(p => {

        p.setOffsetStart(nerStart)
        p.setOffsetEnd(nerEnd)

        if (p.getType == Period.Type.VALUE) {
          val date = Option.apply(dateParser.processing(p.getValue.getRawDate))
          date match {
            case Some(value) =>
              p.getValue.setIsoDate(value.get(0))
            case None =>
              println("Some date are not at all dates.")
          }
        } else if (p.getType == Period.Type.INTERVAL) {
          val fromDate = Option.apply(dateParser.processing(p.getFromDate.getRawDate))
          fromDate match {
            case Some(value) =>
              p.getFromDate.setIsoDate(value.get(0))
            case None =>
              println("Some date are not at all dates.")
          }


          val toDate = Option.apply(dateParser.processing(p.getToDate.getRawDate))
          toDate match {
            case Some(value) =>
              p.getToDate.setIsoDate(value.get(0))
            case None =>
              println("Some date are not at all dates.")
          }

        } else if (p.getType == Period.Type.LIST) {
          JavaConverters.asScalaBufferConverter(p.getList).asScala.toList.map(dw => {
            val date = Option.apply(dateParser.processing(dw.getRawDate))
            date match {
              case Some(value) =>
                dw.setIsoDate(value.get(0))
              case None =>
                println("Some date are not at all dates.")
            }
          })
        }
      })
      //      println(periodsScala)
      periodsScala
    })

  }
}

object DemoPeriod {
  def main(args: Array[String]): Unit = {
    val grobidHome = "/Users/lfoppiano/development/inria/grobid/grobid-home"

    if (args.length != 1) {
      println("Please provide the file to be processed as first argument. ")
      sys.exit(-1)
    }

    val grobidHomeFinder = new GrobidHomeFinder(util.Arrays.asList(grobidHome))

    grobidHomeFinder.findGrobidHomeOrFail();
    GrobidProperties.getInstance(grobidHomeFinder)
    LibraryLoader.load()

    val src = Source.fromFile(args(0))
    val text: String = src.mkString

    val nerParsers = new NERParsers()
    val dateParser = new DateParser()
    val multiDateParser = new TemporalExpressionParser()

    new Parser(nerParsers.getParser("en"), multiDateParser, dateParser).parse(text)

  }
}

object DemoDate {
  def main(args: Array[String]): Unit = {

    if (args.length != 1) {
      println("Please provide the file to be processed as first argument. ")
      sys.exit(-1)
    }

    val grobidHome = "/Users/lfoppiano/development/inria/grobid/grobid-home"

    val grobidHomeFinder = new GrobidHomeFinder(util.Arrays.asList(grobidHome))

    grobidHomeFinder.findGrobidHomeOrFail();
    GrobidProperties.getInstance(grobidHomeFinder)
    LibraryLoader.load()

    val dateParser = new DateParser()
    val multiDateParser = new TemporalExpressionParser()

    val src = Source.fromFile(args(0))
    val iter = src.getLines()

    iter.foreach(l => println(l))

    print("<dates>")
    iter.foreach(line => {
      println("<!-- " + line + " -->")
      val dates = multiDateParser.process(line)
      val datesScala = JavaConverters.asScalaBufferConverter(dates).asScala.toList

      if (dates != null) datesScala.foreach(d => println(d.toXml + " "))

      println()
    })
    print("</dates>")
  }
}
