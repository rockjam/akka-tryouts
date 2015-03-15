import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._

name := "scala-ws"
version := "1.0"
sbtVersion := "0.13.8"
scalaVersion := "2.11.6"
mainClass := Some("Main")
incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(true)
promptTheme := ScalapenosTheme
javaOptions ++= Seq(
  "-XX:+UseParallelGC",
  "-noverify"
)
scalacOptions ++= Seq(
  "-Ydelambdafy:method",
  "-target:jvm-1.7",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps"
)
resolvers ++=  Seq(
  "spray repo" at "http://repo.spray.io/"
)
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.9" exclude ("org.scala-lang" , "scala-library"),
  "io.spray" %% "spray-can" % "1.3.2",
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-json" % "1.3.1" exclude ("org.scala-lang" , "scala-library"),
  "com.wandoulabs.akka" %% "spray-websocket" % "0.1.4",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
seq(Revolver.settings: _*)

addCommandAlias("start", ";re-start;~products")
