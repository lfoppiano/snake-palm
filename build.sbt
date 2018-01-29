name := "snake-palm"

version := "0.0.1"

scalaVersion := "2.12.3"

resolvers += Classpaths.typesafeReleases
resolvers += "Bintray Rookies Repository" at "https://dl.bintray.com/rookies/maven"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime",
  "com.typesafe.akka" %% "akka-http" % "10.0.10"
)

// Local maven repository
resolvers += Resolver.mavenLocal

// GROBID - for now unmanaged in ./lib directory
libraryDependencies ++= Seq(
  "org.grobid" % "grobid-core" % "0.5.1"
//  "org.grobid" % "grobid-ner" % "0.5.0.2-SNAPSHOT"
//  "com.google.code.findbugs" % "jsr305" % "2.0.2"
)

mainClass in (Compile, run) := Some("org.snake.controller.WebServer")

//excludeDependencies += "org.slf4j" %% "slf4j-log4j12"
//excludeDependencies += "log4j" %% "log4j"
//excludeDependencies += "com.rockymadden.stringmetric" %% "stringmetric-core" % "0.27.3"
