name := "snake-palm"

version := "0.0.1"

scalaVersion := "2.10.6"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.5" % "runtime"
)

// Local maven repository
resolvers += Resolver.mavenLocal

// GROBID
libraryDependencies += "org.grobid" % "grobid-core" % "0.4.3-SNAPSHOT"
libraryDependencies += "org.grobid" % "grobid-ner" % "0.4.3-SNAPSHOT"

excludeDependencies += "org.slf4j" %% "slf4j-log4j12"
excludeDependencies += "log4j" %% "log4j"
//excludeDependencies += "com.rockymadden.stringmetric" %% "stringmetric-core" % "0.27.3"
