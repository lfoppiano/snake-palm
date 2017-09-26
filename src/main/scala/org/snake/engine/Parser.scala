package org.snake.engine

import java.io.InputStream
import java.util

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

  def parse(is: InputStream): Unit = {

    val text: String = scala.io.Source.fromInputStream(is).mkString
    parse(text)
  }

  def parse(text: String): Unit = {
    val entities = nerParser.extractNE(text)

    val entitiesScala = JavaConverters.asScalaBufferConverter(entities).asScala.toList

    println("All")
    entitiesScala.foreach(x => println(x))

    val periods = {
      entitiesScala.filter(e => e.getType == NER_Type.PERIOD)
    }

    println("Periods")
    periods.map(x => {
      println(x.getRawName)
      multiDateParser.process(x.getRawName)
      dateParser.processing(x.getRawName)
    })
  }
}

object DemoPeriod {
  def main(args: Array[String]): Unit = {
    val grobidHome = "/Users/lfoppiano/development/inria/grobid/grobid-home"

    if(args.length != 1) {
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

    if(args.length != 1) {
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
