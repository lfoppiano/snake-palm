package org.snake.engine

import java.io.InputStream
import java.util

import org.grobid.core.data.Date
import org.grobid.core.data.dates.Period
import org.grobid.core.engines.{DateParser, MultiDateParser, NERParser, NERParsers}
import org.grobid.core.lexicon.NERLexicon.NER_Type
import org.grobid.core.main.{GrobidHomeFinder, LibraryLoader}
import org.grobid.core.utilities.GrobidProperties

import scala.collection.JavaConverters
import scala.io.Source

class Parser(parser: NERParser, intervalParser: MultiDateParser, dParser: DateParser) {

  var nerParser: NERParser = parser
  var dateParser: DateParser = dParser
  var multiDateParser: MultiDateParser = intervalParser

  def parse(is: InputStream): List[Period] = {

    val text: String = scala.io.Source.fromInputStream(is).mkString
    parse(text)
  }

  def parse(text: String): List[Period] = {
    val entities = nerParser.extractNE(text)

    val entitiesScala = JavaConverters.asScalaBufferConverter(entities).asScala.toList

    println("All Named Entities")
    entitiesScala.foreach(x => println(x))

    val nerPeriods = {
      entitiesScala.filter(e => e.getType == NER_Type.PERIOD)
    }

    println("Only periods and dates")
    return nerPeriods.flatMap(x => {
      //      println(x.getRawName)
      val periods = multiDateParser.process(x.getRawName)
      val periodsScala = JavaConverters.asScalaBufferConverter(periods).asScala.toList

      periodsScala.foreach(p => {
        if (p.getType == Period.Type.VALUE) {
          val date = dateParser.processing(p.getValue.getRawDate)
          p.getValue.setIsoDate(date.get(0))
        } else if(p.getType == Period.Type.INTERVAL) {
          val fromDate = dateParser.processing(p.getFromDate.getRawDate).get(0)
          p.getFromDate.setIsoDate(fromDate)
          val toDate = dateParser.processing(p.getToDate.getRawDate).get(0)
          p.getFromDate.setIsoDate(toDate)
        } else if(p.getType == Period.Type.LIST) {
          JavaConverters.asScalaBufferConverter(p.getList).asScala.toList.map(dw => {
            dw.setIsoDate(dateParser.processing(dw.getRawDate).get(0))
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
    val multiDateParser = new MultiDateParser()

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
    val multiDateParser = new MultiDateParser()

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
