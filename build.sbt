import AssemblyKeys._

name := "open-transcoder"

version := "1.0"

scalaVersion := "2.11.5"

organization := "ariel-networks"

homepage := Some(url("http://www.ariel-networks.com/"))

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/mit-license.php/"))

scalacOptions ++= (
  "-language:postfixOps" ::
    "-language:implicitConversions" ::
    "-language:higherKinds" ::
    "-language:existentials" ::
    "-deprecation" ::
    "-unchecked" ::
    "-Xlint" ::
    "-Ywarn-unused-import" ::
    "-Ywarn-unused" ::
    Nil
  )

resolvers += Opts.resolver.sonatypeReleases

resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)

libraryDependencies ++= (
  ("org.json4s" %% "json4s-native" % "3.2.11") ::
    ("org.json4s" %% "json4s-jackson" % "3.2.11") ::
    ("com.github.scala-incubator.io" %% "scala-io-core" % "0.4.3") ::
    ("com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3") ::
    ("com.typesafe" % "config" % "1.2.1") ::
    ("com.typesafe.scala-logging" %% "scala-logging" % "3.1.0") ::
    ("ch.qos.logback" % "logback-classic" % "1.0.7") ::
    ("org.scala-sbt" %% "io" % sbtVersion.value) ::
    ("org.scalatest" % "scalatest_2.11" % "2.2.1" % "test") ::
    Nil
  )

assemblySettings

jarName in assembly := { s"${name.value}.jar" }

